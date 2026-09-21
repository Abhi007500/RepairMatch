package com.repairmatch.modules.technician.api;

import com.repairmatch.modules.technician.dto.TechnicianProfileDto;
import com.repairmatch.modules.technician.service.TechnicianService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/technicians/public")
public class PublicTechnicianController {

    private final TechnicianService technicianService;

    public PublicTechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnicianProfileDto> getPublicProfile(@PathVariable String id) {
        return ResponseEntity.ok(technicianService.getPublicProfile(id));
    }
}
