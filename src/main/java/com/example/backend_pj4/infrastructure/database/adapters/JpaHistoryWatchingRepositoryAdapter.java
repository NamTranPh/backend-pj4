package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.HistoryWatching;
import com.example.backend_pj4.domain.repository.HistoryWatchingRepository;
import com.example.backend_pj4.domain.repository.filter.HistoryFilter;
import com.example.backend_pj4.infrastructure.database.mappers.HistoryWatchingPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.HistoryWatchingJpaRepository;

@Repository
public class JpaHistoryWatchingRepositoryAdapter implements HistoryWatchingRepository {

    private final HistoryWatchingJpaRepository jpaRepository;
    private final HistoryWatchingPersistenceMapper mapper;

    public JpaHistoryWatchingRepositoryAdapter(HistoryWatchingJpaRepository jpaRepository,
                                               HistoryWatchingPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public HistoryWatching save(HistoryWatching history) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(history)));
    }

    @Override
    public Optional<HistoryWatching> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<HistoryWatching> find(HistoryFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                jpaRepository.search(filter.userId(), filter.q(), filter.unfinishedOnly(),
                        PageableMapper.toPageable(query)),
                mapper::toDomain);
    }

    @Override
    public Optional<HistoryWatching> findByUserIdAndMovieIdAndEpisodeIsNull(String userId, String movieId) {
        return jpaRepository.findByUser_IdAndMovie_IdAndEpisodeIsNull(userId, movieId).map(mapper::toDomain);
    }

    @Override
    public Optional<HistoryWatching> findByUserIdAndMovieId(String userId, String movieId) {
        return jpaRepository.findByUser_IdAndMovie_Id(userId, movieId).map(mapper::toDomain);
    }

    @Override
    public Optional<HistoryWatching> findByUserIdAndMovieIdAndEpisodeId(String userId, String movieId,
                                                                        String episodeId) {
        return jpaRepository.findByUser_IdAndMovie_IdAndEpisode_Id(userId, movieId, episodeId)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByIdAndUserId(String id, String userId) {
        jpaRepository.deleteByIdAndUserId(id, userId);
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId) {
        jpaRepository.deleteByUserId(userId);
    }
}
