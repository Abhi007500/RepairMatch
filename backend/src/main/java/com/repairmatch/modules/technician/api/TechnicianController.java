package com.repairmatch.modules.technician.api;

import com.repairmatch.modules.technician.dto.TechnicianProfileDto;
import com.repairmatch.modules.technician.dto.UpdateTechnicianProfileRequest;
import com.repairmatch.modules.technician.service.TechnicianService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/technician")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @GetMapping("/profile")
    public ResponseEntity<TechnicianProfileDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(technicianService.getMyProfile(authentication.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<TechnicianProfileDto> updateProfile(
            Authentication authentication,
            @RequestBody UpdateTechnicianProfileRequest request) {
        return ResponseEntity.ok(technicianService.updateMyProfile(authentication.getName(), request));
    }

    @PatchMapping("/availability")
    public ResponseEntity<Void> setAvailability(
            Authentication authentication,
            @RequestParam boolean available) {
        technicianService.setAvailability(authentication.getName(), available);
        return ResponseEntity.ok().build();
    }
}
