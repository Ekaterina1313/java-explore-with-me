package ru.practicum.ewm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.util.List;

@RestController
@RequestMapping("/statApi")
public class StatsController {
    private final StatsClient statsClient;

    @Autowired
    public StatsController(StatsClient statsClient) {
        this.statsClient = statsClient;
    }

    @PostMapping("/hit")
    public ResponseEntity<String> hitEndpoint(@RequestBody EndpointHit endpointHit) {
        statsClient.saveEndpointHit(endpointHit);
        return ResponseEntity.status(201).body("Информация сохранена");
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getStats(
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique
    ) {
        List<ViewStats> stats = statsClient.getStats(start, end, uris, unique);
        return ResponseEntity.status(200).body(stats);
    }
}