package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.ListFavorite;
import com.example.backend_pj4.domain.repository.FavoriteRepository;
import com.example.backend_pj4.domain.repository.filter.FavoriteFilter;
import com.example.backend_pj4.infrastructure.database.mappers.ListFavoritePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.ListFavoriteJpaRepository;

@Repository
public class JpaFavoriteRepositoryAdapter implements FavoriteRepository {

    private final ListFavoriteJpaRepository jpaRepository;
    private final ListFavoritePersistenceMapper mapper;

    public JpaFavoriteRepositoryAdapter(ListFavoriteJpaRepository jpaRepository,
                                        ListFavoritePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ListFavorite save(ListFavorite favorite) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(favorite)));
    }

    @Override
    public Optional<ListFavorite> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<ListFavorite> find(FavoriteFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                jpaRepository.searchByUserId(filter.userId(), filter.q(), PageableMapper.toPageable(query)),
                mapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndMovieId(String userId, String movieId) {
        return jpaRepository.existsByUser_IdAndMovie_Id(userId, movieId);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndMovieId(String userId, String movieId) {
        jpaRepository.deleteByUserIdAndMovieId(userId, movieId);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
}
