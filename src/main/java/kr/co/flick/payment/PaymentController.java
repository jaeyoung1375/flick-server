package kr.co.flick.payment;

import kr.co.flick.payment.dto.PaymentConfirmDto;
import kr.co.flick.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/success")
    public void success(@RequestBody PaymentConfirmDto confirmDto){
        paymentService.onSuccess(confirmDto);
    }
}
