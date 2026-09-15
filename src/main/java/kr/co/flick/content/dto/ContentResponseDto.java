package kr.co.flick.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponseDto {
    
    @Schema(description = "작품아이디")
    private Long contentId;

    @Schema(description = "제목")
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @Schema(description = "줄거리")
    private String synopsis;

    @Schema(description = "포스터이미지 파일아이디")
    private Long thumbnailFileId;

    @Schema(description = "개봉일")
    private LocalDate releaseDate;

    @Schema(description = "연령등급")
    private String ageRating;

    @Schema(description = "타입 (MOVIE/SERIES)")
    private String contentType;

    @Schema(description = "공개상태 (DRAFT/PUBLISHED/HIDDEN)")
    private String publishStatus;

}
