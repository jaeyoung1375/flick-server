package kr.co.motive.common.file.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.co.motive.common.file.service.FileService;
import kr.co.motive.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileController {

	private final FileService fileService;

	@PostMapping("/file/editor-image")
	public ApiResponse<String> uploadEditorImage(@RequestPart("file") MultipartFile file, @RequestPart("tempKey") String tempKey) throws IOException{

		String url = fileService.upload(file, tempKey);

		return ApiResponse.ok(url);
	}

}
