package com.example.backend_pj4.application.port.in.history;

import java.util.Set;

import com.example.backend_pj4.application.dto.history.WatchHistoryResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.HistoryFilter;

/**
 * Cổng ĐỌC lịch sử xem.
 *
 * <p>Gộp hai use case cũ {@code GetWatchHistoryUseCase} và {@code GetContinueWatchingUseCase}:
 * chúng chỉ khác nhau ở một điều kiện lọc, nay nằm trong
 * {@link HistoryFilter#unfinishedOnly()}. Cùng DTO, cùng cách nạp phim/tập theo lô.
 */
public interface ListWatchHistoryQuery {

    Set<String> SORTABLE = Set.of("watchedAt", "progress");
    String DEFAULT_SORT = "watchedAt";

    PageResult<WatchHistoryResult> execute(HistoryFilter filter, PageQuery query);
}
