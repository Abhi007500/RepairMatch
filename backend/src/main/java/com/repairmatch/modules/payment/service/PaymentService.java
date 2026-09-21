package com.repairmatch.modules.payment.service;

import com.repairmatch.common.exception.BadRequestException;
import com.repairmatch.common.exception.ResourceNotFoundException;
import com.repairmatch.modules.booking.domain.Booking;
import com.repairmatch.modules.booking.domain.BookingTimeline;
import com.repairmatch.modules.booking.repository.BookingRepository;
import com.repairmatch.modules.booking.repository.BookingTimelineRepository;
import com.repairmatch.modules.payment.domain.Payment;
import com.repairmatch.modules.payment.dto.*;
import com.repairmatch.modules.payment.repository.PaymentRepository;
import com.repairmatch.modules.user.domain.User;
import com.repairmatch.modules.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingTimelineRepository timelineRepository;
    private final UserRepository userRepository;
    private final RazorpayPaymentGatewayProvider paymentGatewayProvider;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            BookingTimelineRepository timelineRepository,
            UserRepository userRepository,
            RazorpayPaymentGatewayProvider paymentGatewayProvider) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.timelineRepository = timelineRepository;
        this.userRepository = userRepository;
        this.paymentGatewayProvider = paymentGatewayProvider;
    }

    @Transactional
    public PaymentOrderResponse createPaymentOrder(String userEmail, CreatePaymentOrderRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findByIdWithDetails(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));

        // Authorization check: only customer or admin can initiate payment
        boolean isOwner = booking.getCustomer().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to pay for this booking.");
        }

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus()) || "REJECTED".equalsIgnoreCase(booking.getStatus())) {
            throw new BadRequestException("Cannot initiate payment for a cancelled or rejected booking.");
        }

        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            throw new BadRequestException("Booking has already been paid for.");
        }

        BigDecimal amount = (booking.getFinalAmount() != null && booking.getFinalAmount().compareTo(BigDecimal.ZERO) > 0)
                ? booking.getFinalAmount()
                : booking.getInspectionFee();

        String receipt = "rcpt_" + booking.getBookingReference();
        Map<String, String> notes = Map.of(
                "bookingId", booking.getId(),
                "bookingReference", booking.getBookingReference(),
                "customerId", user.getId()
        );

        String providerOrderId = paymentGatewayProvider.createOrder(receipt, amount, "INR", notes);

        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                booking,
                booking.getCustomer(),
                amount,
                "INR",
                "RAZORPAY",
                "RAZORPAY",
                providerOrderId
        );

        Payment savedPayment = paymentRepository.save(payment);

        return new PaymentOrderResponse(
                savedPayment.getId(),
                booking.getId(),
                booking.getBookingReference(),
                amount,
                "INR",
                "RAZORPAY",
                providerOrderId,
                paymentGatewayProvider.getKeyId(),
                booking.getCustomer().getFullName(),
                booking.getCustomer().getEmail(),
                booking.getCustomer().getPhoneNumber()
        );
    }

    @Transactional(noRollbackFor = BadRequestException.class)
    public PaymentResponse verifyPayment(String userEmail, VerifyPaymentRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + request.getPaymentId()));

        Booking booking = payment.getBooking();

        // Authorization check
        boolean isCustomer = booking.getCustomer().getId().equals(user.getId());
        boolean isTech = booking.getTechnician().getUser().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        if (!isCustomer && !isTech && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to verify this payment.");
        }

        // Idempotency: If already marked PAID, return existing without re-processing
        if ("PAID".equalsIgnoreCase(payment.getPaymentStatus())) {
            return PaymentResponse.fromEntity(payment);
        }

        // Signature validation
        boolean isValidSignature = paymentGatewayProvider.verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!isValidSignature) {
            payment.setPaymentStatus("FAILED");
            payment.setFailureReason("Cryptographic signature verification failed");
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            throw new BadRequestException("Invalid payment signature. Payment verification failed.");
        }

        // Mark payment as PAID
        payment.setPaymentStatus("PAID");
        payment.setProviderPaymentId(request.getRazorpayPaymentId());
        payment.setProviderSignature(request.getRazorpaySignature());
        payment.setFailureReason(null);
        payment.setUpdatedAt(LocalDateTime.now());
        Payment updatedPayment = paymentRepository.save(payment);

        // Update booking payment status
        booking.setPaymentStatus("PAID");
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // Add timeline entry
        BookingTimeline timeline = new BookingTimeline(
                UUID.randomUUID().toString(),
                booking,
                "PAID",
                "Online payment of ₹" + payment.getAmount() + " successfully verified via Razorpay (Payment ID: " + request.getRazorpayPaymentId() + ")"
        );
        timelineRepository.save(timeline);

        return PaymentResponse.fromEntity(updatedPayment);
    }

    @Transactional
    public PaymentResponse recordPaymentFailure(String userEmail, FailPaymentRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + request.getPaymentId()));

        payment.setPaymentStatus("FAILED");
        payment.setFailureReason(request.getReason() != null ? request.getReason() : "Payment cancelled or declined");
        payment.setUpdatedAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        return PaymentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsForBooking(String userEmail, String bookingId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        boolean isCustomer = booking.getCustomer().getId().equals(user.getId());
        boolean isTech = booking.getTechnician().getUser().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        if (!isCustomer && !isTech && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to view payment details for this booking.");
        }

        return paymentRepository.findByBookingId(bookingId).stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
