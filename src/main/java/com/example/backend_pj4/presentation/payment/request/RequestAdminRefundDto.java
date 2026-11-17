package com.example.backend_pj4.presentation.payment.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.backend_pj4.common.constants.enums.RefundStatus;
import com.example.backend_pj4.domain.repository.filter.PaymentRefundFilter;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAdminRefundDto extends PageQueryParamDto {

    private RefundStatus status;
    private String paymentOrderId;
    private String userId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    public PaymentRefundFilter toFilter() {
        return new PaymentRefundFilter(
                status,
                paymentOrderId,
                userId,
                fromDate,
                toDate,
                search()
        );
    }
}
