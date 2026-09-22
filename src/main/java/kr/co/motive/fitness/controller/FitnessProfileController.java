package kr.co.motive.fitness.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.motive.common.response.ApiResponse;
import kr.co.motive.common.util.SecurityUtil;
import kr.co.motive.fitness.dto.UserFitnessProfileInsertDto;
import kr.co.motive.fitness.dto.UserFitnessProfileResponseDto;
import kr.co.motive.fitness.dto.UserFitnessProfileUpdateDto;
import kr.co.motive.fitness.service.FitnessProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "운동프로필 Controller", description = "회원 운동프로필 컨트롤러")
public class FitnessProfileController {

	private final FitnessProfileService fitnessProfileService;

	@Operation(summary = "내 운동프로필 조회", description = "로그인한 회원의 운동프로필을 조회한다")
	@GetMapping("/fitness-profile")
	public ApiResponse<UserFitnessProfileResponseDto> getProfile() {

		return ApiResponse.ok(fitnessProfileService.getProfile(SecurityUtil.getUserId()));
	}

	@Operation(summary = "내 운동프로필 등록", description = "로그인한 회원의 운동프로필을 등록한다")
	@PostMapping("/fitness-profile")
	public ApiResponse<Void> insertProfile(@Valid @RequestPart("data") UserFitnessProfileInsertDto dto,
			@RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

		fitnessProfileService.insertProfile(SecurityUtil.getUserId(), dto, profileImage);
		return ApiResponse.ok();
	}

	@Operation(summary = "내 운동프로필 수정", description = "로그인한 회원의 운동프로필을 수정한다")
	@PutMapping("/fitness-profile")
	public ApiResponse<Void> updateProfile(@Valid @RequestPart("data") UserFitnessProfileUpdateDto dto,
			@RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

		fitnessProfileService.updateProfile(SecurityUtil.getUserId(), dto, profileImage);
		return ApiResponse.ok();
	}

}