package com.repairmatch.modules.review.api;

import com.repairmatch.modules.review.dto.CreateReviewRequest;
import com.repairmatch.modules.review.dto.ReviewDto;
import com.repairmatch.modules.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            Authentication authentication,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewDto review = reviewService.createReview(authentication.getName(), request);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<ReviewDto>> getTechnicianReviews(@PathVariable String technicianId) {
        return ResponseEntity.ok(reviewService.getTechnicianReviews(technicianId));
    }
}
