package kr.co.motive.menu.controller;

import java.util.List;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.motive.common.response.ApiResponse;
import kr.co.motive.menu.dto.MenuDto;
import kr.co.motive.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Menu", description = "메뉴 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MenuController {

	private final MenuService menuService;

	@Operation(summary = "메뉴 조회", description = "전체 메뉴를 조회합니다")
	@GetMapping("/public/menus")
	public ApiResponse<List<MenuDto>> menuList() {
		return ApiResponse.ok(menuService.menuList());

	}

}
