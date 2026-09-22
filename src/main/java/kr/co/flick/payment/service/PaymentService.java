package kr.co.flick.payment.service;

import kr.co.flick.auth.util.JwtTokenUtil;
import kr.co.flick.common.util.JsonUtil;
import kr.co.flick.common.util.SecurityUtil;
import kr.co.flick.payment.dto.PaymentConfirmDto;
import kr.co.flick.payment.dto.PaymentHistoryDto;
import kr.co.flick.payment.mapper.PaymentMapper;
import kr.co.flick.toss.dto.TossConfirmResultDto;
import kr.co.flick.toss.service.TossPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final TossPaymentService tossPaymentService;

    public void onSuccess(PaymentConfirmDto confirmDto) {

        Long userId = SecurityUtil.getUserId();

        String rawJson = tossPaymentService.confirm(confirmDto);

        TossConfirmResultDto resultDto = JsonUtil.fromJson(rawJson, TossConfirmResultDto.class);

        PaymentHistoryDto historyDto = PaymentHistoryDto
                .builder()
                .pgTransactionId(resultDto.getOrderId())
                .paymentKey(resultDto.getPaymentKey())
                .status(resultDto.getStatus())
                .approvedAt(resultDto.getApprovedAt())
                .rawResponse(rawJson)
                .userId(userId)
                .amount(confirmDto.getAmount())
                .build();

        paymentMapper.insertPaymentHistory(historyDto);
    }
}
