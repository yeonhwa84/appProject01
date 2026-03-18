package com.visualnovel.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.*;

import java.sql.*;
import java.util.Map;

/**
 * MyBatis JSON 타입 핸들러
 * DB의 JSON 컬럼 ↔ Map<String,Integer> 자동 변환
 */
@MappedTypes(Map.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonTypeHandler extends BaseTypeHandler<Map<String, Integer>> {

    private static final ObjectMapper om = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
            Map<String, Integer> param, JdbcType jdbcType) throws SQLException {
        try { ps.setString(i, om.writeValueAsString(param)); }
        catch (Exception e) { ps.setString(i, "{}"); }
    }

    @Override
    public Map<String, Integer> getNullableResult(ResultSet rs, String col) throws SQLException {
        return parse(rs.getString(col));
    }

    @Override
    public Map<String, Integer> getNullableResult(ResultSet rs, int idx) throws SQLException {
        return parse(rs.getString(idx));
    }

    @Override
    public Map<String, Integer> getNullableResult(CallableStatement cs, int idx) throws SQLException {
        return parse(cs.getString(idx));
    }

    private Map<String, Integer> parse(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try { return om.readValue(json, new TypeReference<>() {}); }
        catch (Exception e) { return Map.of(); }
    }
}
