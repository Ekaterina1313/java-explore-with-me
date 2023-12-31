package ru.practicum.mainService.mapper;

import ru.practicum.mainService.model.EndpointHit;

import java.time.LocalDateTime;

public class EndpointHitMapper {
    public static EndpointHit createEndpointHit(String app, String clientIp, String endpointPath) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(app);
        endpointHit.setUri(endpointPath);
        endpointHit.setIp(clientIp);
        endpointHit.setTimestamp(LocalDateTime.now());
        return endpointHit;
    }
}