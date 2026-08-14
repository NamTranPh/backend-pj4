package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.domain.model.ListFavorite;
import com.example.backend_pj4.domain.repository.FavoriteRepository;
import com.example.backend_pj4.infrastructure.database.mappers.ListFavoritePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.ListFavoriteJpaRepository;

@Repository
public class JpaFavoriteRepositoryAdapter implements FavoriteRepository {

    private final ListFavoriteJpaRepository jpaRepository;
    private final ListFavoritePersistenceMapper mapper;

    public JpaFavoriteRepositoryAdapter(ListFavoriteJpaRepository jpaRepository, ListFavoritePersistenceMapper mapper) {
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
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<ListFavorite> findByUserId(String userId) {
        return jpaRepository.findByUser_Id(userId).stream().map(mapper::toDomain).collect(Collectors.toList());
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
    public long countByUserId(String userId) {
        return jpaRepository.countByUser_Id(userId);
    }
}


