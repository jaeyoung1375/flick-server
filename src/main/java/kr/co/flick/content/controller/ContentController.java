package kr.co.flick.content.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.flick.common.response.ApiResponse;
import kr.co.flick.configuration.PublicController;
import kr.co.flick.content.dto.ContentResponseDto;
import kr.co.flick.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Content", description = "콘텐츠 카탈로그 API")
@RestController
@PublicController
public class ContentController {

    private final ContentService contentService;

    @Operation(summary = "작품 목록 조회", description = "공개된 작품 목록을 조회합니다")
    @GetMapping("/contents")
    public ApiResponse<List<ContentResponseDto>> getContentList() {
        return ApiResponse.ok(contentService.getContentList());
    }
}
