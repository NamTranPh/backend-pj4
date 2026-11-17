package com.example.backend_pj4.presentation.payment.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.domain.repository.filter.PaymentOrderFilter;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAdminPaymentDto extends PageQueryParamDto {

    private PaymentOrderStatus status;
    private String gateway;
    private String membershipPlanId;
    private String userId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    public PaymentOrderFilter toFilter() {
        return new PaymentOrderFilter(
                status,
                gateway,
                membershipPlanId,
                userId,
                fromDate,
                toDate,
                search()
        );
    }
}
