package ru.practicum.mainService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentShortDto {
    private Integer id;
    private String text;
    private Integer authorId;
}
