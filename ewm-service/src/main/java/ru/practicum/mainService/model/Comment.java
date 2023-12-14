package ru.practicum.mainService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 5000)
    private String text;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @Column(name = "created")
    private LocalDateTime created;
}
