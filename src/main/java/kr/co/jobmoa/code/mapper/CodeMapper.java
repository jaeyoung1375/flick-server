package kr.co.jobmoa.code.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.jobmoa.code.dto.CodeRequestDto;
import kr.co.jobmoa.code.dto.CodeResponseDto;

@Mapper
public interface CodeMapper {

	/**
	 * 코드 다건 조회
	 * @param comCdIds
	 * @return
	 */
	List<CodeResponseDto> getCodeList(CodeRequestDto dto);


}
