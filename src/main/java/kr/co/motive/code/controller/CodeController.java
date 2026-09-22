package kr.co.motive.code.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.motive.code.dto.CodeRequestDto;
import kr.co.motive.code.dto.CodeResponseDto;
import kr.co.motive.code.service.CodeService;
import kr.co.motive.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "공통코드 Controller", description = "공통코드 컨트롤러")
public class CodeController {

	private final CodeService codeService;

	@Operation(summary = "공통코드 다건 조회", description = "공통코드 다건을 조회한다")
	@GetMapping("/public/codes")
	public ApiResponse<Map<String, List<CodeResponseDto>>> getCodeList(@ModelAttribute CodeRequestDto dto) {

		return ApiResponse.ok(codeService.getCodeList(dto));
	}

	@Operation(summary = "공통코드 단건 조회", description = "공통코드 단건을 조회한다")
	@GetMapping("/public/code")
	public ApiResponse<List<CodeResponseDto>> getCode(@ModelAttribute CodeRequestDto dto) {

		return ApiResponse.ok(codeService.getCode(dto));
	}





}
