package com.repairmatch.modules.booking.service;

import com.repairmatch.common.exception.BadRequestException;
import com.repairmatch.common.exception.ResourceNotFoundException;
import com.repairmatch.modules.booking.domain.Booking;
import com.repairmatch.modules.booking.domain.BookingTimeline;
import com.repairmatch.modules.booking.dto.BookingDto;
import com.repairmatch.modules.booking.dto.BookingTimelineDto;
import com.repairmatch.modules.booking.dto.CancelBookingRequest;
import com.repairmatch.modules.booking.dto.CreateBookingRequest;
import com.repairmatch.modules.booking.dto.UpdateBookingStatusRequest;
import com.repairmatch.modules.booking.repository.BookingRepository;
import com.repairmatch.modules.booking.repository.BookingTimelineRepository;
import com.repairmatch.modules.catalog.domain.Brand;
import com.repairmatch.modules.catalog.domain.Category;
import com.repairmatch.modules.catalog.domain.Model;
import com.repairmatch.modules.catalog.domain.ProblemType;
import com.repairmatch.modules.catalog.repository.BrandRepository;
import com.repairmatch.modules.catalog.repository.CategoryRepository;
import com.repairmatch.modules.catalog.repository.ModelRepository;
import com.repairmatch.modules.catalog.repository.ProblemTypeRepository;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import com.repairmatch.modules.user.domain.Address;
import com.repairmatch.modules.user.domain.User;
import com.repairmatch.modules.user.repository.AddressRepository;
import com.repairmatch.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingTimelineRepository timelineRepository;
    private final UserRepository userRepository;
    private final TechnicianProfileRepository technicianProfileRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final ProblemTypeRepository problemTypeRepository;
    private final AddressRepository addressRepository;

    public BookingService(
            BookingRepository bookingRepository,
            BookingTimelineRepository timelineRepository,
            UserRepository userRepository,
            TechnicianProfileRepository technicianProfileRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ModelRepository modelRepository,
            ProblemTypeRepository problemTypeRepository,
            AddressRepository addressRepository) {
        this.bookingRepository = bookingRepository;
        this.timelineRepository = timelineRepository;
        this.userRepository = userRepository;
        this.technicianProfileRepository = technicianProfileRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.problemTypeRepository = problemTypeRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public BookingDto createBooking(String customerEmail, CreateBookingRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + customerEmail));

        TechnicianProfile technician = technicianProfileRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + request.getTechnicianId()));

        if (!"VERIFIED".equalsIgnoreCase(technician.getVerificationStatus())) {
            throw new BadRequestException("Technician is not yet verified to accept service bookings.");
        }

        if (!technician.isAvailable()) {
            throw new BadRequestException("Technician is currently marked as unavailable.");
        }

        // Check slot conflict
        boolean hasConflict = bookingRepository.existsByTechnicianIdAndScheduledDateAndTimeSlotAndStatusIn(
                technician.getId(),
                request.getScheduledDate(),
                request.getTimeSlot(),
                List.of("PENDING", "ACCEPTED", "IN_PROGRESS")
        );
        if (hasConflict) {
            throw new BadRequestException("Technician already has an active booking for " +
                    request.getScheduledDate() + " during slot " + request.getTimeSlot());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + request.getAddressId()));

        Brand brand = request.getBrandId() != null ? brandRepository.findById(request.getBrandId()).orElse(null) : null;
        Model model = request.getModelId() != null ? modelRepository.findById(request.getModelId()).orElse(null) : null;
        ProblemType problemType = request.getProblemTypeId() != null ? problemTypeRepository.findById(request.getProblemTypeId()).orElse(null) : null;

        String bookingRef = "RM-2026-" + String.format("%04d", new Random().nextInt(10000));

        Booking booking = new Booking();
        booking.setId(UUID.randomUUID().toString());
        booking.setBookingReference(bookingRef);
        booking.setCustomer(customer);
        booking.setTechnician(technician);
        booking.setCategory(category);
        booking.setBrand(brand);
        booking.setModel(model);
        booking.setProblemType(problemType);
        booking.setProblemDescription(request.getProblemDescription().trim());
        booking.setAddress(address);
        booking.setScheduledDate(request.getScheduledDate());
        booking.setTimeSlot(request.getTimeSlot());
        booking.setStatus("PENDING");
        booking.setInspectionFee(technician.getBaseInspectionFee());
        booking.setPaymentStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        BookingTimeline timeline = new BookingTimeline(
                UUID.randomUUID().toString(),
                savedBooking,
                "PENDING",
                "Service booking initiated by customer"
        );
        timelineRepository.save(timeline);

        return loadWithTimeline(savedBooking.getId());
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(String userEmail, String bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        return loadWithTimeline(booking.getId());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getCustomerBookings(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findCustomerBookingsWithDetails(customer.getId()).stream()
                .map(b -> loadWithTimeline(b.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getTechnicianBookings(String technicianEmail) {
        User techUser = userRepository.findByEmail(technicianEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TechnicianProfile profile = technicianProfileRepository.findByUserId(techUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician profile not found"));

        return bookingRepository.findTechnicianBookingsWithDetails(profile.getId()).stream()
                .map(b -> loadWithTimeline(b.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto updateBookingStatus(String userEmail, String bookingId, UpdateBookingStatusRequest request) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        String targetStatus = request.getStatus().toUpperCase();
        BookingStateMachine.validateTransition(booking.getStatus(), targetStatus);

        booking.setStatus(targetStatus);
        booking.setUpdatedAt(LocalDateTime.now());

        if ("COMPLETED".equals(targetStatus)) {
            BigDecimal amount = request.getFinalAmount() != null ? request.getFinalAmount() : booking.getInspectionFee();
            booking.setFinalAmount(amount);
            booking.setPaymentStatus("PAID");

            // Increment completed jobs on technician profile
            TechnicianProfile tech = booking.getTechnician();
            tech.setCompletedJobsCount(tech.getCompletedJobsCount() + 1);
            technicianProfileRepository.save(tech);
        }

        bookingRepository.save(booking);

        String remarks = request.getRemarks() != null ? request.getRemarks() : ("Status changed to " + targetStatus);
        BookingTimeline timeline = new BookingTimeline(
                UUID.randomUUID().toString(),
                booking,
                targetStatus,
                remarks
        );
        timelineRepository.save(timeline);

        return loadWithTimeline(booking.getId());
    }

    @Transactional
    public BookingDto cancelBooking(String userEmail, String bookingId, CancelBookingRequest request) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        BookingStateMachine.validateTransition(booking.getStatus(), "CANCELLED");

        booking.setStatus("CANCELLED");
        booking.setCancellationReason(request.getReason());
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        BookingTimeline timeline = new BookingTimeline(
                UUID.randomUUID().toString(),
                booking,
                "CANCELLED",
                "Cancelled: " + request.getReason()
        );
        timelineRepository.save(timeline);

        return loadWithTimeline(booking.getId());
    }

    private BookingDto loadWithTimeline(String bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        List<BookingTimelineDto> timeline = timelineRepository.findByBookingIdOrderByCreatedAtAsc(bookingId).stream()
                .map(BookingTimelineDto::fromEntity)
                .collect(Collectors.toList());

        BookingDto dto = BookingDto.fromEntity(booking);
        dto.setTimeline(timeline);
        return dto;
    }
}
