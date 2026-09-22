package kr.co.motive.common.util;

import java.util.List;

import com.github.pagehelper.PageInfo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponseDto<T> {
    private List<T> data;
    private int pageSize;
    private int pages;
    private int pageNum;
    private long total;
    private long startRow;

    public static <T> PageResponseDto<T> of(List<T> list) {
    	PageInfo<T> pageInfo = new PageInfo<>(list);
        return PageResponseDto.<T>builder()
            .data(pageInfo.getList())
            .pageSize(pageInfo.getPageSize())
            .pages(pageInfo.getPages())
            .pageNum(pageInfo.getPageNum())
            .total(pageInfo.getTotal())
            .startRow(pageInfo.getStartRow())
            .build();
    }
}