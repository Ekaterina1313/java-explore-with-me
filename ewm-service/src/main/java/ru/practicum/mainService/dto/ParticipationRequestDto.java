package ru.practicum.mainService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipationRequestDto {
    private String created;
    private Integer event;
    private Integer id;
    private Integer requester;
    private String status;
}