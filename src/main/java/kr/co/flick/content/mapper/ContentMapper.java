package kr.co.flick.content.mapper;

import kr.co.flick.content.dto.ContentResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContentMapper {

    /**
     * 작품 목록 조회.
     * @return
     */
    List<ContentResponseDto> getContentList();

    /**
     * 작품 상세 조회.
     * @param contentId
     * @return
     */
    ContentResponseDto selectContentById(String contentId);
}
