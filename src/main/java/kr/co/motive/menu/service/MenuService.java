package kr.co.motive.menu.service;

import java.util.List;


import org.springframework.stereotype.Service;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.motive.menu.dto.MenuDto;
import kr.co.motive.menu.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuService {

	private final MenuMapper menuMapper;

	@Operation(summary = "메뉴 조회", description = "전체 메뉴를 조회합니다")
	public List<MenuDto> menuList(){
		return menuMapper.menuList();
	}



}
