package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.filter.EpisodeFilter;
import com.example.backend_pj4.infrastructure.database.mappers.EpisodePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.EpisodeJpaRepository;

@Repository
public class JpaEpisodeRepositoryAdapter implements EpisodeRepository {

    private final EpisodeJpaRepository episodeJpaRepository;
    private final EpisodePersistenceMapper episodePersistenceMapper;

    public JpaEpisodeRepositoryAdapter(EpisodeJpaRepository episodeJpaRepository,
                                       EpisodePersistenceMapper episodePersistenceMapper) {
        this.episodeJpaRepository = episodeJpaRepository;
        this.episodePersistenceMapper = episodePersistenceMapper;
    }

    @Override
    public Episode save(Episode episode) {
        return episodePersistenceMapper.toDomain(
                episodeJpaRepository.save(episodePersistenceMapper.toEntity(episode)));
    }

    @Override
    public Optional<Episode> findById(String id) {
        return episodeJpaRepository.findById(id).map(episodePersistenceMapper::toDomain);
    }

    @Override
    public List<Episode> findByIds(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return episodeJpaRepository.findAllById(ids).stream()
                .map(episodePersistenceMapper::toDomain).toList();
    }

    @Override
    public PageResult<Episode> find(EpisodeFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                episodeJpaRepository.searchByMovieId(
                        filter.movieId(), filter.q(), PageableMapper.toPageable(query)),
                episodePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Episode> findByMovieIdAndEpisodeNumber(String movieId, Integer episodeNumber) {
        return episodeJpaRepository.findByMovie_IdAndEpisodeNumber(movieId, episodeNumber)
                .map(episodePersistenceMapper::toDomain);
    }

    @Override
    public List<Episode> findByMovieIdOrderByEpisodeNumber(String movieId) {
        return episodeJpaRepository.findByMovie_IdOrderByEpisodeNumberAsc(movieId).stream()
                .map(episodePersistenceMapper::toDomain).toList();
    }

    @Override
    public long countByMovieIdAndStatus(String movieId, VideoStatus status) {
        return episodeJpaRepository.countByMovie_IdAndStatus(movieId, status);
    }

    @Override
    public void deleteById(String id) {
        episodeJpaRepository.deleteById(id);
    }
}
