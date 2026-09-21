package com.repairmatch.modules.admin.api;

import com.repairmatch.modules.auth.dto.UserDto;
import com.repairmatch.modules.booking.repository.BookingRepository;
import com.repairmatch.modules.technician.dto.TechnicianProfileDto;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import com.repairmatch.modules.technician.service.TechnicianService;
import com.repairmatch.modules.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TechnicianService technicianService;
    private final UserRepository userRepository;
    private final TechnicianProfileRepository technicianProfileRepository;
    private final BookingRepository bookingRepository;

    public AdminController(
            TechnicianService technicianService,
            UserRepository userRepository,
            TechnicianProfileRepository technicianProfileRepository,
            BookingRepository bookingRepository) {
        this.technicianService = technicianService;
        this.userRepository = userRepository;
        this.technicianProfileRepository = technicianProfileRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/technicians/pending")
    public ResponseEntity<List<TechnicianProfileDto>> getPendingTechnicians() {
        return ResponseEntity.ok(technicianService.getPendingTechnicians());
    }

    @PutMapping("/technicians/{id}/verify")
    public ResponseEntity<TechnicianProfileDto> verifyTechnician(
            @PathVariable String id,
            @RequestParam(defaultValue = "VERIFIED") String status) {
        return ResponseEntity.ok(technicianService.updateVerificationStatus(id, status));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPlatformStats() {
        long totalUsers = userRepository.count();
        long totalVerifiedTechs = technicianProfileRepository.findByVerificationStatus("VERIFIED").size();
        long pendingKyc = technicianProfileRepository.findByVerificationStatus("PENDING").size();
        long totalBookings = bookingRepository.count();

        return ResponseEntity.ok(Map.of(
                "totalUsers", totalUsers,
                "verifiedTechnicians", totalVerifiedTechs,
                "pendingKycApprovals", pendingKyc,
                "totalBookings", totalBookings
        ));
    }
}
