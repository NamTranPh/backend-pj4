package com.example.backend_pj4.application.command.movie;

import java.util.List;
import java.util.Set;

import com.example.backend_pj4.application.command.actor.MovieActorAssignmentCommand;
import com.example.backend_pj4.common.constants.enums.AgeRating;
import com.example.backend_pj4.common.constants.enums.AudioTrackType;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoQuality;

public record CreateMovieCommand(
        String title,
        String originalTitle,
        String description,
        Integer releaseYear,
        Integer duration,
        String director,
        List<MovieActorAssignmentCommand> movieActors,
        String country,
        String language,
        String trailerUrl,
        MovieType movieType,
        Integer totalEpisodes,
        AgeRating ageRating,
        Set<VideoQuality> videoQualities,
        Set<AudioTrackType> audioTracks,
        List<String> genreIds,
        String createdByUserId
) {
}
