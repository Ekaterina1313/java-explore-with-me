package ru.practicum.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewCompilationDto {
    private Integer id;
    private List<Integer> events;
    private String title;
    private Boolean pinned;
}