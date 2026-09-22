package kr.co.flick.payment.mapper;

import kr.co.flick.payment.dto.PaymentHistoryDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    int insertPaymentHistory(PaymentHistoryDto dto);
}
