package kr.co.motive.admin.code.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.motive.admin.code.dto.AdminCodeDetailInsertDto;
import kr.co.motive.admin.code.dto.AdminCodeDetailResponseDto;
import kr.co.motive.admin.code.dto.AdminCodeDetailUpdateDto;
import kr.co.motive.admin.code.dto.AdminCodeInsertDto;
import kr.co.motive.admin.code.dto.AdminCodeResponseDto;
import kr.co.motive.admin.code.dto.AdminCodeUpdateDto;

@Mapper
public interface AdminCodeMapper {

	/**
	 * 공통코드 목록 조회
	 * @return
	 */
	List<AdminCodeResponseDto> getCodeList();

	/**
	 * 상세코드 목록 조회
	 * @param comCdId
	 * @return
	 */
	List<AdminCodeDetailResponseDto> getCodeDetailList(@Param("comCdId") String comCdId);

	/**
	 * 공통코드 존재 여부 조회
	 * @param comCdId
	 * @return
	 */
	int existsComCd(@Param("comCdId") String comCdId);

	/**
	 * 공통코드 등록
	 * @param dto
	 * @param regId
	 */
	void insertComCd(@Param("dto") AdminCodeInsertDto dto, @Param("regId") String regId);

	/**
	 * 공통코드 수정
	 * @param comCdId
	 * @param dto
	 * @param modId
	 * @return 수정된 행 수
	 */
	int updateComCd(@Param("comCdId") String comCdId, @Param("dto") AdminCodeUpdateDto dto, @Param("modId") String modId);

	/**
	 * 상세코드 존재 여부 조회
	 * @param comCdId
	 * @param dtlCdId
	 * @return
	 */
	int existsDtlCd(@Param("comCdId") String comCdId, @Param("dtlCdId") String dtlCdId);

	/**
	 * 상세코드 등록
	 * @param comCdId
	 * @param dto
	 * @param regId
	 */
	void insertDtlCd(@Param("comCdId") String comCdId, @Param("dto") AdminCodeDetailInsertDto dto, @Param("regId") String regId);

	/**
	 * 상세코드 수정
	 * @param comCdId
	 * @param dtlCdId
	 * @param dto
	 * @param modId
	 * @return 수정된 행 수
	 */
	int updateDtlCd(@Param("comCdId") String comCdId, @Param("dtlCdId") String dtlCdId, @Param("dto") AdminCodeDetailUpdateDto dto, @Param("modId") String modId);

	/**
	 * 공통코드 삭제
	 * @param comCdId
	 * @return 삭제된 행 수
	 */
	int deleteComCd(@Param("comCdId") String comCdId);

	/**
	 * 공통코드에 속한 상세코드 전체 삭제
	 * @param comCdId
	 */
	void deleteDtlCdByComCdId(@Param("comCdId") String comCdId);

	/**
	 * 상세코드 삭제
	 * @param comCdId
	 * @param dtlCdId
	 * @return 삭제된 행 수
	 */
	int deleteDtlCd(@Param("comCdId") String comCdId, @Param("dtlCdId") String dtlCdId);

}