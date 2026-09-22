package kr.co.flick.menu.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.flick.menu.dto.MenuDto;

@Mapper
public interface MenuMapper {

	/* 메뉴목록 조회 */
	List<MenuDto> menuList();
}
