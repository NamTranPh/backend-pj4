package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movie_actor", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_movie_actor", columnNames = {"movie_id", "actor_id"})
    },
    indexes = {
        @Index(name = "idx_movie_actor_movie", columnList = "movie_id"),
        @Index(name = "idx_movie_actor_actor", columnList = "actor_id")
    }
)
@Getter
@Setter
public class MovieActorJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieJpaEntity movie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private ActorJpaEntity actor;

    @Column(name = "character_name", length = 255)
    private String characterName;

    @Column(name = "order_index")
    private Integer orderIndex = 0;

    public MovieActorJpaEntity() {}

    public MovieActorJpaEntity(MovieJpaEntity movie, ActorJpaEntity actor, String characterName, Integer orderIndex) {
        this.movie = movie;
        this.actor = actor;
        this.characterName = characterName;
        this.orderIndex = orderIndex != null ? orderIndex : 0;
    }
}
