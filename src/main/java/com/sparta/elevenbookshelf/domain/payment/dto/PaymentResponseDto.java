package com.sparta.elevenbookshelf.domain.payment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentResponseDto {

    private String tid;         // 카카오페이에서 제공하는 결제 고유 ID
    private String pgToken;     // 결제 승인 토큰
    private String orderId;     // 주문 ID
    private Long userId;        // 사용자 ID
    private String status;      // 결제 상태

    @Builder
    public PaymentResponseDto(String tid, String pgToken, String orderId, Long userId, String status) {
        this.tid = tid;
        this.pgToken = pgToken;
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
    }

    // 상태가 필요 없는 경우를 위한 오버로드된 생성자
    public PaymentResponseDto(String tid, String pgToken, String orderId, Long userId) {
        this.tid = tid;
        this.pgToken = pgToken;
        this.orderId = orderId;
        this.userId = userId;
        this.status = null;
    }
}
