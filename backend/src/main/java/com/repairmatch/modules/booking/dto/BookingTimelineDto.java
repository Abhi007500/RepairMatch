package com.repairmatch.modules.booking.dto;

import com.repairmatch.modules.booking.domain.BookingTimeline;

import java.time.LocalDateTime;

public class BookingTimelineDto {
    private String id;
    private String status;
    private String remarks;
    private LocalDateTime createdAt;

    public BookingTimelineDto() {}

    public static BookingTimelineDto fromEntity(BookingTimeline bt) {
        BookingTimelineDto dto = new BookingTimelineDto();
        dto.id = bt.getId();
        dto.status = bt.getStatus();
        dto.remarks = bt.getRemarks();
        dto.createdAt = bt.getCreatedAt();
        return dto;
    }

    public String getId() { return id; }
    public String getStatus() { return status; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
