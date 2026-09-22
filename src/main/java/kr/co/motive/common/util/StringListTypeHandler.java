package kr.co.motive.common.util;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(List.class)
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

 @Override
 public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
     String value = rs.getString(columnName);
     if (value == null || value.isEmpty()) return new ArrayList<>();
     return new ArrayList<>(Arrays.asList(value.split(", ")));
 }

 @Override
 public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {}

 @Override
 public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
     String value = rs.getString(columnIndex);
     if (value == null || value.isEmpty()) return new ArrayList<>();
     return new ArrayList<>(Arrays.asList(value.split(", ")));
 }

 @Override
 public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
     String value = cs.getString(columnIndex);
     if (value == null || value.isEmpty()) return new ArrayList<>();
     return new ArrayList<>(Arrays.asList(value.split(", ")));
 }
}