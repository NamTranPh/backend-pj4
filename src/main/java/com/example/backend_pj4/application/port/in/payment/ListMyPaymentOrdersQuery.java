package com.example.backend_pj4.application.port.in.payment;

import java.util.Set;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;

/** Cổng ĐỌC danh sách đơn thanh toán của người dùng. */
public interface ListMyPaymentOrdersQuery {

    Set<String> SORTABLE = Set.of("createdAt", "price", "status");
    String DEFAULT_SORT = "createdAt";

    PageResult<PaymentOrderResult> execute(String userId, PageQuery query);
}
