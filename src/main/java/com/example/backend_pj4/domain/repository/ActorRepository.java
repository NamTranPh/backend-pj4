package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Actor;
import com.example.backend_pj4.domain.repository.filter.ActorFilter;

public interface ActorRepository {
    Actor save(Actor actor);
    PageResult<Actor> find(ActorFilter filter, PageQuery query);
    Optional<Actor> findById(String id);
    List<Actor> findByIds(List<String> ids);
    boolean existsByName(String name);
    void deleteById(String id);
}
