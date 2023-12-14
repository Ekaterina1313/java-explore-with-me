package ru.practicum.mainService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Length(min = 3, max = 120)
    @Column(nullable = false)
    private String title;
    @Length(min = 20, max = 2000)
    @Column(nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Length(min = 20, max = 7000)
    @Column(nullable = false)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column(name = "location_lat", nullable = false)
    private Double locationLat;
    @Column(name = "location_lon", nullable = false)
    private Double locationLon;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_on")
    private LocalDateTime createdOn = LocalDateTime.now();
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean paid;
    @Column(nullable = false, columnDefinition = "integer default 0", name = "participant_limit")
    private Integer participantLimit;
    @Column(nullable = false, columnDefinition = "boolean default false", name = "  request_moderation")
    private Boolean requestModeration;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_state")
    private States state = States.PENDING;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;
    @Column(nullable = false)
    private Integer views;
}