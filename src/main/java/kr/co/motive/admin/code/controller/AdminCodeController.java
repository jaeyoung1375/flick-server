package kr.co.motive.admin.code.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.motive.admin.code.dto.AdminCodeDetailInsertDto;
import kr.co.motive.admin.code.dto.AdminCodeDetailResponseDto;
import kr.co.motive.admin.code.dto.AdminCodeDetailUpdateDto;
import kr.co.motive.admin.code.dto.AdminCodeInsertDto;
import kr.co.motive.admin.code.dto.AdminCodeResponseDto;
import kr.co.motive.admin.code.dto.AdminCodeUpdateDto;
import kr.co.motive.admin.code.service.AdminCodeService;
import kr.co.motive.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "관리자 공통코드 Controller", description = "관리자 공통코드 컨트롤러")
public class AdminCodeController {

	private final AdminCodeService adminCodeService;

	@Operation(summary = "공통코드 목록 조회", description = "관리자용 공통코드 목록을 조회한다")
	@GetMapping("/admin/codes")
	public ApiResponse<List<AdminCodeResponseDto>> getCodeList() {

		return ApiResponse.ok(adminCodeService.getCodeList());
	}

	@Operation(summary = "상세코드 목록 조회", description = "공통코드에 속한 상세코드 목록을 조회한다")
	@GetMapping("/admin/codes/{comCdId}/details")
	public ApiResponse<List<AdminCodeDetailResponseDto>> getCodeDetailList(@PathVariable String comCdId) {

		return ApiResponse.ok(adminCodeService.getCodeDetailList(comCdId));
	}

	@Operation(summary = "공통코드 등록", description = "공통코드를 등록한다")
	@PostMapping("/admin/codes")
	public ApiResponse<Void> insertCode(@Valid @RequestBody AdminCodeInsertDto dto) {

		adminCodeService.insertCode(dto);
		return ApiResponse.ok();
	}

	@Operation(summary = "공통코드 수정", description = "공통코드를 수정한다")
	@PutMapping("/admin/codes/{comCdId}")
	public ApiResponse<Void> updateCode(@PathVariable String comCdId, @Valid @RequestBody AdminCodeUpdateDto dto) {

		adminCodeService.updateCode(comCdId, dto);
		return ApiResponse.ok();
	}

	@Operation(summary = "상세코드 등록", description = "공통코드에 속한 상세코드를 등록한다")
	@PostMapping("/admin/codes/{comCdId}/details")
	public ApiResponse<Void> insertCodeDetail(@PathVariable String comCdId, @Valid @RequestBody AdminCodeDetailInsertDto dto) {

		adminCodeService.insertCodeDetail(comCdId, dto);
		return ApiResponse.ok();
	}

	@Operation(summary = "상세코드 수정", description = "상세코드를 수정한다")
	@PutMapping("/admin/codes/{comCdId}/details/{dtlCdId}")
	public ApiResponse<Void> updateCodeDetail(@PathVariable String comCdId, @PathVariable String dtlCdId,
			@Valid @RequestBody AdminCodeDetailUpdateDto dto) {

		adminCodeService.updateCodeDetail(comCdId, dtlCdId, dto);
		return ApiResponse.ok();
	}

	@Operation(summary = "공통코드 삭제", description = "공통코드를 삭제한다 (속한 상세코드도 함께 삭제된다)")
	@DeleteMapping("/admin/codes/{comCdId}")
	public ApiResponse<Void> deleteCode(@PathVariable String comCdId) {

		adminCodeService.deleteCode(comCdId);
		return ApiResponse.ok();
	}

	@Operation(summary = "상세코드 삭제", description = "상세코드를 삭제한다")
	@DeleteMapping("/admin/codes/{comCdId}/details/{dtlCdId}")
	public ApiResponse<Void> deleteCodeDetail(@PathVariable String comCdId, @PathVariable String dtlCdId) {

		adminCodeService.deleteCodeDetail(comCdId, dtlCdId);
		return ApiResponse.ok();
	}

}