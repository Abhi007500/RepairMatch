package com.repairmatch.modules.booking.api;

import com.repairmatch.modules.booking.dto.BookingDto;
import com.repairmatch.modules.booking.dto.CancelBookingRequest;
import com.repairmatch.modules.booking.dto.CreateBookingRequest;
import com.repairmatch.modules.booking.dto.UpdateBookingStatusRequest;
import com.repairmatch.modules.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            Authentication authentication,
            @Valid @RequestBody CreateBookingRequest request) {
        BookingDto booking = bookingService.createBooking(authentication.getName(), request);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(
            Authentication authentication,
            @PathVariable String id) {
        return ResponseEntity.ok(bookingService.getBookingById(authentication.getName(), id));
    }

    @GetMapping("/customer")
    public ResponseEntity<List<BookingDto>> getCustomerBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getCustomerBookings(authentication.getName()));
    }

    @GetMapping("/technician")
    public ResponseEntity<List<BookingDto>> getTechnicianBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getTechnicianBookings(authentication.getName()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BookingDto> updateBookingStatus(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody UpdateBookingStatusRequest request) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(authentication.getName(), id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancelBooking(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody CancelBookingRequest request) {
        return ResponseEntity.ok(bookingService.cancelBooking(authentication.getName(), id, request));
    }
}
