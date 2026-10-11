package com.vision.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface SysLogMapper {
    
    @Select("SELECT * FROM sys_log ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> findByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);
    
    @Select("SELECT COUNT(*) FROM sys_log")
    Long countAll();
    
    @Insert("INSERT INTO sys_log(user_id, username, operation, method, params, ip) " +
            "VALUES(#{userId}, #{username}, #{operation}, #{method}, #{params}, #{ip})")
    int insert(Map<String, Object> log);
}