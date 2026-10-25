package com.example.backend_pj4.application.port.in.actor;

import java.util.Set;

import com.example.backend_pj4.application.dto.actor.ActorResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.ActorFilter;

public interface ListActorsQuery {

    Set<String> SORTABLE = Set.of("createdAt", "name", "birthDate");
    String DEFAULT_SORT = "createdAt";

    PageResult<ActorResult> execute(ActorFilter filter, PageQuery query);
}
