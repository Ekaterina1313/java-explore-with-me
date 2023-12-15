package ru.practicum.mainService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDto {
    private Integer id;
    private String text;
    private UserShortDto author;
    private Integer eventId;
    private String created;
}