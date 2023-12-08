package ru.practicum.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipationRequestDto {
    private String created;
    private Integer eventId;
    private Integer id;
    private Integer requesterId;
    private String status;
}