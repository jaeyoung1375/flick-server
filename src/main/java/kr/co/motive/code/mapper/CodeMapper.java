package kr.co.motive.code.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.motive.code.dto.CodeRequestDto;
import kr.co.motive.code.dto.CodeResponseDto;

@Mapper
public interface CodeMapper {

	/**
	 * 코드 다건 조회
	 * @param comCdIds
	 * @return
	 */
	List<CodeResponseDto> getCodeList(CodeRequestDto dto);


}
