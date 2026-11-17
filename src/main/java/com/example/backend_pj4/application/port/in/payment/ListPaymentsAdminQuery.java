package com.example.backend_pj4.application.port.in.payment;

import java.util.Set;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.PaymentOrderFilter;

public interface ListPaymentsAdminQuery {
    Set<String> SORTABLE = Set.of("createdAt", "price", "status", "requestedAt", "completedAt");
    String DEFAULT_SORT = "createdAt";

    PageResult<PaymentOrderResult> execute(PaymentOrderFilter filter, PageQuery query);
}
