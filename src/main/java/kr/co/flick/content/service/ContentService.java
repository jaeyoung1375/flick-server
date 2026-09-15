package kr.co.flick.content.service;

import kr.co.flick.content.dto.ContentResponseDto;
import kr.co.flick.content.mapper.ContentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContentService {

    private final ContentMapper contentMapper;

    public List<ContentResponseDto> getContentList() {

        return contentMapper.getContentList();
    }
}
