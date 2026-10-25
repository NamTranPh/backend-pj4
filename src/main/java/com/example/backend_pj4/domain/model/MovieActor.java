package com.example.backend_pj4.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class MovieActor {
    private String id;
    private String movieId;
    private String actorId;
    private Actor actor;
    private String characterName;
    private Integer orderIndex;
}
