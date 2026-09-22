package kr.co.motive.common.file.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.motive.common.file.dto.FileDto;

@Mapper
public interface FileMapper {

	void insertFile(FileDto file);

	void confirmTempFiles(String tempKey);

	List<FileDto> selectTempFiles(String tempKey);
}
