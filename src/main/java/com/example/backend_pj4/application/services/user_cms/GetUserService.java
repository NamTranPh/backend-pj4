package com.example.backend_pj4.application.services.user_cms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.user_cms.RequestGetUserCmsDto;
import com.example.backend_pj4.application.dto.response.user_cms.UserCmsResponse;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.common.enums.SortOrder;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;
import com.example.backend_pj4.infrastructure.databases.mapper.UserMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetUserService extends BaseService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Object> execute(RequestGetUserCmsDto dto) {
        // --- Default values ---
        int page = Optional.ofNullable(dto.getPage()).orElse(1);
        int limit = Optional.ofNullable(dto.getLimit()).orElse(10);
        String sortBy = Optional.ofNullable(dto.getSortBy()).orElse("createdAt");
        SortOrder sortOrder = Optional.ofNullable(dto.getSortOrder()).orElse(SortOrder.desc);
        String q = dto.getQ();

        // --- Base query ---
        StringBuilder queryStr = new StringBuilder("SELECT u FROM UserEntity u WHERE 1=1");

        // --- SEARCH: q ---
        if (q != null && !q.isEmpty()) {
            queryStr.append(" AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%'))")
                    .append(" OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))")
                    .append(" OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :q, '%')))");
        }

        // --- SORT ---
        queryStr.append(" ORDER BY u.").append(sortBy).append(" ").append(sortOrder.name());

        // --- MAIN QUERY ---
        TypedQuery<UserEntity> query = entityManager.createQuery(queryStr.toString(), UserEntity.class);

        if (q != null && !q.isEmpty()) {
            query.setParameter("q", q);
        }

        // --- PAGINATION ---
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);

        // --- MAP ENTITY -> DOMAIN -> DTO ---
        List<UserCmsResponse> items = query.getResultList().stream()
                .map(userMapper::toDomain) // entity -> domain
                .map(UserCmsResponse::fromDomain) // domain -> DTO
                .toList();

        // --- COUNT QUERY ---
        String countStr = queryStr.toString()
                .replaceFirst("SELECT u FROM UserEntity u", "SELECT COUNT(u) FROM UserEntity u")
                .replaceFirst("ORDER BY.+$", "");

        TypedQuery<Long> countQuery = entityManager.createQuery(countStr, Long.class);
        if (q != null && !q.isEmpty()) {
            countQuery.setParameter("q", q);
        }

        long totalItems = countQuery.getSingleResult();

        // --- RESPONSE ---
        Map<String, Object> pagination = Map.of(
                "page", page,
                "limit", limit,
                "totalItems", totalItems,
                "totalPages", (int) Math.ceil((double) totalItems / limit));

        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("pagination", pagination);

        return result;
    }

    public Optional<User> executeSingle(String userId) {
        return userRepository.findById(userId);
    }
}
