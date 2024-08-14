package com.sparta.elevenbookshelf.domain.payment.controller;

import com.sparta.elevenbookshelf.domain.payment.dto.PaymentRequestDto;
import com.sparta.elevenbookshelf.domain.payment.dto.PaymentResponseDto;
import com.sparta.elevenbookshelf.domain.payment.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    // KakaoPayService 의존성을 주입받음
    private final KakaoPayService kakaoPayService;

    /**
     * 카카오페이 결제 준비를 처리하는 엔드포인트
     *
     * @param paymentRequestDto 결제 요청 데이터
     * @return PaymentResponseDto 결제 준비 결과
     */
    @PostMapping("/kakao")
    public ResponseEntity<PaymentResponseDto> createKakaoPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto paymentResponseDto = kakaoPayService.createKakaoPayment(paymentRequestDto);
        return ResponseEntity.ok(paymentResponseDto);
    }

    /**
     * 결제 승인 완료 시 호출되는 엔드포인트
     *
     * @param tid 결제 고유 ID
     * @param orderId 주문 ID
     * @param pgToken 결제 승인 토큰
     * @return PaymentResponseDto 결제 승인 결과
     */
    @GetMapping("/kakao/success")
    public ResponseEntity<PaymentResponseDto> kakaoPaymentSuccess(@RequestParam("tid") String tid,
                                                                  @RequestParam("orderId") String orderId,
                                                                  @RequestParam("pg_token") String pgToken) {
        PaymentResponseDto paymentResponseDto = kakaoPayService.handleKakaoPaymentSuccess(tid, orderId, pgToken);
        return ResponseEntity.ok(paymentResponseDto);
    }

    /**
     * 결제 상태를 조회하는 엔드포인트
     *
     * @param tid 결제 고유 ID
     * @return PaymentResponseDto 결제 상태 정보
     */
    @GetMapping("/kakao/status")
    public ResponseEntity<PaymentResponseDto> getKakaoPaymentStatus(@RequestParam("tid") String tid) {
        PaymentResponseDto paymentResponseDto = kakaoPayService.getPaymentStatus(tid);
        return ResponseEntity.ok(paymentResponseDto);
    }

    /**
     * 결제 취소를 처리하는 엔드포인트
     *
     * @param tid 결제 고유 ID
     * @param cancelAmount 취소할 금액
     * @return PaymentResponseDto 결제 취소 결과
     */
    @PostMapping("/kakao/cancel")
    public ResponseEntity<PaymentResponseDto> cancelKakaoPayment(@RequestParam("tid") String tid,
                                                                 @RequestParam("cancelAmount") String cancelAmount) {
        PaymentResponseDto paymentResponseDto = kakaoPayService.cancelKakaoPayment(tid, cancelAmount);
        return ResponseEntity.ok(paymentResponseDto);
    }
}
