package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.infrastructure.database.mappers.EpisodePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.SpringDataEpisodeRepository;

@Repository
public class JpaEpisodeRepositoryAdapter implements EpisodeRepository {

    private final SpringDataEpisodeRepository SpringDataEpisodeRepository;
    private final EpisodePersistenceMapper EpisodePersistenceMapper;

    public JpaEpisodeRepositoryAdapter(SpringDataEpisodeRepository SpringDataEpisodeRepository, EpisodePersistenceMapper EpisodePersistenceMapper) {
        this.SpringDataEpisodeRepository = SpringDataEpisodeRepository;
        this.EpisodePersistenceMapper = EpisodePersistenceMapper;
    }

    @Override
    public Episode save(Episode episode) {
        return EpisodePersistenceMapper.toDomain(SpringDataEpisodeRepository.save(EpisodePersistenceMapper.toEntity(episode)));
    }

    @Override
    public Optional<Episode> findById(String id) {
        return SpringDataEpisodeRepository.findById(id).map(EpisodePersistenceMapper::toDomain);
    }

    @Override
    public List<Episode> findAll() {
        return SpringDataEpisodeRepository.findAll().stream().map(EpisodePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        SpringDataEpisodeRepository.deleteById(id);
    }

    @Override
    public List<Episode> findByMovieId(String movieId) {
        return SpringDataEpisodeRepository.findByMovie_Id(movieId).stream().map(EpisodePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Episode> findByMovieIdAndEpisodeNumber(String movieId, Integer episodeNumber) {
        return SpringDataEpisodeRepository.findByMovie_IdAndEpisodeNumber(movieId, episodeNumber).map(EpisodePersistenceMapper::toDomain);
    }

    @Override
    public List<Episode> findByMovieIdOrderByEpisodeNumber(String movieId) {
        return SpringDataEpisodeRepository.findByMovie_IdOrderByEpisodeNumberAsc(movieId).stream().map(EpisodePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Episode> findByStatus(VideoStatus status) {
        return SpringDataEpisodeRepository.findByStatus(status).stream().map(EpisodePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByMovieId(String movieId) {
        return SpringDataEpisodeRepository.countByMovie_Id(movieId);
    }
}


