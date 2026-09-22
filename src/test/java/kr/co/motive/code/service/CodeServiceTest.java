package kr.co.motive.code.service;

import kr.co.motive.code.dto.CodeRequestDto;
import kr.co.motive.code.dto.CodeResponseDto;
import kr.co.motive.code.mapper.CodeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

    @Mock
    CodeMapper codeMapper;

    @InjectMocks
    CodeService codeService;

    @Test
    @DisplayName("getCodeList는 comCdId로 그룹핑된 Map을 반환한다")
    void getCodeList_groupsByComCdId() {
        CodeRequestDto req = new CodeRequestDto();
        CodeResponseDto code1 = CodeResponseDto.builder().comCdId("TECH").dtlCdId("JAVA").build();
        CodeResponseDto code2 = CodeResponseDto.builder().comCdId("TECH").dtlCdId("PYTHON").build();
        CodeResponseDto code3 = CodeResponseDto.builder().comCdId("POSITION").dtlCdId("FRONTEND").build();

        when(codeMapper.getCodeList(req)).thenReturn(List.of(code1, code2, code3));

        Map<String, List<CodeResponseDto>> result = codeService.getCodeList(req);

        assertEquals(2, result.size());
        assertEquals(2, result.get("TECH").size());
        assertEquals(1, result.get("POSITION").size());
    }

    @Test
    @DisplayName("getCodeList는 빈 목록에서 빈 Map을 반환한다")
    void getCodeList_returnsEmptyMapWhenNoData() {
        CodeRequestDto req = new CodeRequestDto();
        when(codeMapper.getCodeList(req)).thenReturn(List.of());

        Map<String, List<CodeResponseDto>> result = codeService.getCodeList(req);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getCodeList는 단일 그룹도 올바르게 처리한다")
    void getCodeList_singleGroup() {
        CodeRequestDto req = new CodeRequestDto();
        CodeResponseDto code = CodeResponseDto.builder().comCdId("TECH").dtlCdId("JAVA").build();

        when(codeMapper.getCodeList(req)).thenReturn(List.of(code));

        Map<String, List<CodeResponseDto>> result = codeService.getCodeList(req);

        assertEquals(1, result.size());
        assertEquals("JAVA", result.get("TECH").get(0).getDtlCdId());
    }

    @Test
    @DisplayName("getCode는 mapper 결과를 그대로 반환한다")
    void getCode_returnsFlatList() {
        CodeRequestDto req = new CodeRequestDto();
        CodeResponseDto code = CodeResponseDto.builder().comCdId("TECH").dtlCdId("JAVA").build();

        when(codeMapper.getCodeList(req)).thenReturn(List.of(code));

        List<CodeResponseDto> result = codeService.getCode(req);

        assertEquals(1, result.size());
        assertEquals("JAVA", result.get(0).getDtlCdId());
    }

    @Test
    @DisplayName("getCode는 빈 목록에서 빈 List를 반환한다")
    void getCode_returnsEmptyListWhenNoData() {
        CodeRequestDto req = new CodeRequestDto();
        when(codeMapper.getCodeList(req)).thenReturn(List.of());

        List<CodeResponseDto> result = codeService.getCode(req);

        assertTrue(result.isEmpty());
    }
}
