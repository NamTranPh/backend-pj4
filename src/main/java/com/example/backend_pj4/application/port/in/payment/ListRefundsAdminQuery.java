package com.example.backend_pj4.application.port.in.payment;

import java.util.Set;

import com.example.backend_pj4.application.dto.payment.PaymentRefundResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.PaymentRefundFilter;

public interface ListRefundsAdminQuery {
    Set<String> SORTABLE = Set.of("createdAt", "requestedAt", "processedAt", "amount", "status");
    String DEFAULT_SORT = "createdAt";

    PageResult<PaymentRefundResult> execute(PaymentRefundFilter filter, PageQuery query);
}
