package com.example.backend_pj4.application.query.actor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.actor.ActorResult;
import com.example.backend_pj4.application.mapper.ActorResultMapper;
import com.example.backend_pj4.application.port.in.actor.ListActorsQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.ActorRepository;
import com.example.backend_pj4.domain.repository.filter.ActorFilter;

@Service
public class ListActorsQueryHandler implements ListActorsQuery {

    private final ActorRepository actorRepository;
    private final ActorResultMapper actorResultMapper;

    public ListActorsQueryHandler(ActorRepository actorRepository, ActorResultMapper actorResultMapper) {
        this.actorRepository = actorRepository;
        this.actorResultMapper = actorResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ActorResult> execute(ActorFilter filter, PageQuery query) {
        return actorRepository.find(filter, query).map(actorResultMapper::toResult);
    }
}
