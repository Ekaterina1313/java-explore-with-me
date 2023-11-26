package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsService {
    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public void saveEndpointHit(EndpointHit endpointHit) {
        statsRepository.save(endpointHit);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            if (uris != null && !uris.isEmpty()) {
                return statsRepository.findUniqueStatsByDateRangeAndUris(start, end, uris);
            } else {
                return statsRepository.findUniqueStatsByDateRange(start, end);
            }
        } else {
            if (uris != null && !uris.isEmpty()) {
                return statsRepository.findStatsByDateRangeAndUris(start, end, uris);
            } else {
                return statsRepository.findStatsByDateRange(start, end);
            }
        }
    }
}