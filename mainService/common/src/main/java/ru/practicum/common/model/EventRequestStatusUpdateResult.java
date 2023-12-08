package ru.practicum.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.common.dto.ParticipationRequestDto;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventRequestStatusUpdateResult {

    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}