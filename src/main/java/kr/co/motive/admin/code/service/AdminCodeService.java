package kr.co.motive.admin.code.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.motive.admin.code.dto.AdminCodeDetailInsertDto;
import kr.co.motive.admin.code.dto.AdminCodeDetailResponseDto;
import kr.co.motive.admin.code.dto.AdminCodeDetailUpdateDto;
import kr.co.motive.admin.code.dto.AdminCodeInsertDto;
import kr.co.motive.admin.code.dto.AdminCodeResponseDto;
import kr.co.motive.admin.code.dto.AdminCodeUpdateDto;
import kr.co.motive.admin.code.mapper.AdminCodeMapper;
import kr.co.motive.common.code.CodeErrorCode;
import kr.co.motive.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCodeService {

	// TODO: 관리자 인증 연동 후 SecurityContext에서 로그인한 관리자 아이디를 가져오도록 교체
	private static final String TEMP_ADMIN_ID = "ADMIN";

	private final AdminCodeMapper adminCodeMapper;

	/**
	 * 공통코드 목록 조회
	 * @return
	 */
	public List<AdminCodeResponseDto> getCodeList() {

		return adminCodeMapper.getCodeList();
	}

	/**
	 * 상세코드 목록 조회
	 * @param comCdId
	 * @return
	 */
	public List<AdminCodeDetailResponseDto> getCodeDetailList(String comCdId) {

		return adminCodeMapper.getCodeDetailList(comCdId);
	}

	/**
	 * 공통코드 등록
	 * @param dto
	 */
	public void insertCode(AdminCodeInsertDto dto) {

		if (adminCodeMapper.existsComCd(dto.getComCdId()) > 0) {
			throw new CustomException(CodeErrorCode.CODE_DUPLICATED);
		}

		adminCodeMapper.insertComCd(dto, TEMP_ADMIN_ID);
	}

	/**
	 * 공통코드 수정
	 * @param comCdId
	 * @param dto
	 */
	public void updateCode(String comCdId, AdminCodeUpdateDto dto) {

		int updated = adminCodeMapper.updateComCd(comCdId, dto, TEMP_ADMIN_ID);
		if (updated == 0) {
			throw new CustomException(CodeErrorCode.CODE_NOT_FOUND);
		}
	}

	/**
	 * 상세코드 등록
	 * @param comCdId
	 * @param dto
	 */
	public void insertCodeDetail(String comCdId, AdminCodeDetailInsertDto dto) {

		if (adminCodeMapper.existsComCd(comCdId) == 0) {
			throw new CustomException(CodeErrorCode.CODE_NOT_FOUND);
		}
		if (adminCodeMapper.existsDtlCd(comCdId, dto.getDtlCdId()) > 0) {
			throw new CustomException(CodeErrorCode.DETAIL_CODE_DUPLICATED);
		}

		adminCodeMapper.insertDtlCd(comCdId, dto, TEMP_ADMIN_ID);
	}

	/**
	 * 상세코드 수정
	 * @param comCdId
	 * @param dtlCdId
	 * @param dto
	 */
	public void updateCodeDetail(String comCdId, String dtlCdId, AdminCodeDetailUpdateDto dto) {

		int updated = adminCodeMapper.updateDtlCd(comCdId, dtlCdId, dto, TEMP_ADMIN_ID);
		if (updated == 0) {
			throw new CustomException(CodeErrorCode.DETAIL_CODE_NOT_FOUND);
		}
	}

	/**
	 * 공통코드 삭제 (상세코드도 함께 삭제)
	 * @param comCdId
	 */
	@Transactional
	public void deleteCode(String comCdId) {

		adminCodeMapper.deleteDtlCdByComCdId(comCdId);

		int deleted = adminCodeMapper.deleteComCd(comCdId);
		if (deleted == 0) {
			throw new CustomException(CodeErrorCode.CODE_NOT_FOUND);
		}
	}

	/**
	 * 상세코드 삭제
	 * @param comCdId
	 * @param dtlCdId
	 */
	public void deleteCodeDetail(String comCdId, String dtlCdId) {

		int deleted = adminCodeMapper.deleteDtlCd(comCdId, dtlCdId);
		if (deleted == 0) {
			throw new CustomException(CodeErrorCode.DETAIL_CODE_NOT_FOUND);
		}
	}

}