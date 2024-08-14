package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.PaymentRequestDto;
import com.sparta.elevenbookshelf.dto.PaymentResponseDto;
import com.sparta.elevenbookshelf.entity.Payment;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;

    // 카카오페이 API 인증키를 설정 파일에서 가져옴
    @Value("${kakao.pay.admin-key}")
    private String adminKey;

    // 카카오페이 상점 ID를 설정 파일에서 가져옴 (기본값: TC0ONETIME)
    @Value("${kakao.pay.cid:TC0ONETIME}")
    private String cid;

    // 카카오페이 API 요청 URL과 기타 상수 정의
    private static final String KAKAO_READY_URL = "https://kapi.kakao.com/v1/payment/ready";
    private static final String KAKAO_APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";
    private static final String KAKAO_CANCEL_URL = "https://kapi.kakao.com/v1/payment/cancel";
    private static final String VAT_AMOUNT = "200";
    private static final String TAX_FREE_AMOUNT = "0";

    /**
     * 결제 준비 요청을 처리하는 메서드
     *
     * @param paymentRequestDto 결제 요청 데이터
     * @return PaymentResponseDto 결제 준비 결과
     */
    public PaymentResponseDto createKakaoPayment(PaymentRequestDto paymentRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put("cid", cid);
        params.put("partner_order_id", paymentRequestDto.getOrderId());
        params.put("partner_user_id", paymentRequestDto.getUserId().toString());
        params.put("item_name", "Sample Item");
        params.put("quantity", "1");
        params.put("total_amount", String.valueOf(paymentRequestDto.getAmount()));
        params.put("vat_amount", VAT_AMOUNT);
        params.put("tax_free_amount", TAX_FREE_AMOUNT);
        params.put("approval_url", "http://localhost:8080/api/payments/kakao/success");
        params.put("cancel_url", "http://localhost:8080/api/payments/kakao/cancel");
        params.put("fail_url", "http://localhost:8080/api/payments/kakao/fail");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(KAKAO_READY_URL, request, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            User user = userService.getUser(paymentRequestDto.getUserId());

            Payment payment = Payment.builder()
                    .tid(response.get("tid").toString())
                    .orderId(paymentRequestDto.getOrderId())
                    .user(user)
                    .amount(paymentRequestDto.getAmount())
                    .status("READY")
                    .build();

            paymentRepository.save(payment);

            return new PaymentResponseDto(payment.getTid(), null, payment.getOrderId(), payment.getUser().getId());
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.PAYMENT_FAILURE);
        }
    }

    /**
     * 결제 승인 요청을 처리하는 메서드
     *
     * @param tid 결제 고유 ID
     * @param orderId 주문 ID
     * @param pgToken 결제 승인 토큰
     * @return PaymentResponseDto 결제 승인 결과
     */
    @Transactional
    public PaymentResponseDto handleKakaoPaymentSuccess(String tid, String orderId, String pgToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Payment payment = paymentRepository.findByTid(tid)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        Map<String, String> params = new HashMap<>();
        params.put("cid", cid);
        params.put("tid", tid);
        params.put("partner_order_id", orderId);
        params.put("partner_user_id", payment.getUser().getId().toString());
        params.put("pg_token", pgToken);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        try {
            restTemplate.postForEntity(KAKAO_APPROVE_URL, request, Map.class);
            payment.updateStatus("SUCCESS");

            return new PaymentResponseDto(payment.getTid(), pgToken, payment.getOrderId(), payment.getUser().getId());
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.PAYMENT_APPROVAL_FAILURE);
        }
    }

    /**
     * 결제 상태를 조회하는 메서드
     *
     * @param tid 결제 고유 ID
     * @return PaymentResponseDto 결제 상태 정보
     */
    public PaymentResponseDto getPaymentStatus(String tid) {
        Payment payment = paymentRepository.findByTid(tid)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        return new PaymentResponseDto(payment.getTid(), null, payment.getOrderId(), payment.getUser().getId(), payment.getStatus());
    }

    /**
     * 결제 취소 요청을 처리하는 메서드
     *
     * @param tid 결제 고유 ID
     * @param cancelAmount 취소할 금액
     * @return PaymentResponseDto 결제 취소 결과
     */
    @Transactional
    public PaymentResponseDto cancelKakaoPayment(String tid, String cancelAmount) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put("cid", cid);
        params.put("tid", tid);
        params.put("cancel_amount", cancelAmount);
        params.put("cancel_tax_free_amount", TAX_FREE_AMOUNT);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        try {
            restTemplate.postForEntity(KAKAO_CANCEL_URL, request, Map.class);
            Payment payment = paymentRepository.findByTid(tid)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

            payment.updateStatus("CANCELLED");

            return new PaymentResponseDto(payment.getTid(), null, payment.getOrderId(), payment.getUser().getId());
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.PAYMENT_CANCELLATION_FAILURE);
        }
    }
}