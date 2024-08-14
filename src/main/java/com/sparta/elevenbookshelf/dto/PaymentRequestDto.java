package com.sparta.elevenbookshelf.dto;

import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentRequestDto {

    @NotEmpty
    private String orderId;

    @NotNull
    private Long userId;

    @Min(1) // 최소 결제 금액은 1 이상이어야 함
    private int amount;
}
