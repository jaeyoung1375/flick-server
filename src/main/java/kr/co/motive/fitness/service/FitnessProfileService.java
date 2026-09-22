package kr.co.motive.fitness.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.motive.auth.mapper.AuthMapper;
import kr.co.motive.common.code.FitnessErrorCode;
import kr.co.motive.common.exception.CustomException;
import kr.co.motive.common.file.service.FileService;
import kr.co.motive.fitness.dto.UserFitnessProfileInsertDto;
import kr.co.motive.fitness.dto.UserFitnessProfileResponseDto;
import kr.co.motive.fitness.dto.UserFitnessProfileUpdateDto;
import kr.co.motive.fitness.mapper.FitnessProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FitnessProfileService {

	private final FitnessProfileMapper fitnessProfileMapper;

	private final AuthMapper authMapper;

	private final FileService fileService;

	/**
	 * 운동프로필 조회
	 * @param userId
	 * @return
	 */
	public UserFitnessProfileResponseDto getProfile(Long userId) {

		UserFitnessProfileResponseDto profile = fitnessProfileMapper.getProfile(userId);
		if (profile == null) {
			throw new CustomException(FitnessErrorCode.PROFILE_NOT_FOUND);
		}

		return profile;
	}

	/**
	 * 운동프로필 등록
	 * @param userId
	 * @param dto
	 */
	@Transactional
	public void insertProfile(Long userId, UserFitnessProfileInsertDto dto, MultipartFile profileImage) throws IOException {

		if (fitnessProfileMapper.existsProfile(userId)) {
			throw new CustomException(FitnessErrorCode.PROFILE_ALREADY_EXISTS);
		}

		fitnessProfileMapper.insertProfile(userId, dto);
		authMapper.updateProfileInfo(userId, dto.getNickname(), dto.getGender(), dto.getBirth());

		if (profileImage != null && !profileImage.isEmpty()) {
			Long profileFileId = fileService.uploadProfileImage(profileImage);
			authMapper.updateProfileFileId(userId, profileFileId);
		}
	}


	/**
	 * 운동프로필 수정
	 * @param userId
	 * @param dto
	 */
	public void updateProfile(Long userId, UserFitnessProfileUpdateDto dto, MultipartFile profileImage) throws IOException {

		int updated = fitnessProfileMapper.updateProfile(userId, dto);
		if (updated == 0) {
			throw new CustomException(FitnessErrorCode.PROFILE_NOT_FOUND);
		}

		if (profileImage != null && !profileImage.isEmpty()) {
			Long profileFileId = fileService.uploadProfileImage(profileImage);
			authMapper.updateProfileFileId(userId, profileFileId);
		}
	}

}