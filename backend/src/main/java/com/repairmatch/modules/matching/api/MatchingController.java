package com.repairmatch.modules.matching.api;

import com.repairmatch.modules.matching.dto.MatchRequest;
import com.repairmatch.modules.matching.dto.TechnicianMatchDto;
import com.repairmatch.modules.matching.service.MatchingEngineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingEngineService matchingEngineService;

    public MatchingController(MatchingEngineService matchingEngineService) {
        this.matchingEngineService = matchingEngineService;
    }

    @PostMapping("/find")
    public ResponseEntity<List<TechnicianMatchDto>> findMatches(@Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchingEngineService.findMatches(request));
    }
}
