package kr.co.flick.toss.service;

import kr.co.flick.payment.dto.PaymentConfirmDto;
import kr.co.flick.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final RestClient restClient;

    public String confirm(PaymentConfirmDto confirmDto) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(confirmDto)
                .retrieve()
                .body(String.class);


    }
}
