package com.vision.mapper;

import com.vision.entity.SysUser;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

        @Select("SELECT * FROM sys_user WHERE username = #{username}")
        SysUser findByUsername(String username);

        @Select("SELECT * FROM sys_user WHERE id = #{id}")
        SysUser findById(Long id);

        @Select("SELECT * FROM sys_user WHERE role = #{role}")
        List<SysUser> findByRole(String role);

        @Select("SELECT * FROM sys_user WHERE status = 1")
        List<SysUser> findAllActive();

        @Select("SELECT id, username, real_name, role, phone, email, status, create_time, update_time FROM sys_user ORDER BY create_time DESC")
        List<SysUser> findAllUsers();

        @Insert("INSERT INTO sys_user(username, password, real_name, role, phone, email, status) " +
                        "VALUES(#{username}, #{password}, #{realName}, #{role}, #{phone}, #{email}, 1)")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(SysUser user);

        /**
         * 更新用户信息（不含密码）
         */
        @Update("UPDATE sys_user SET real_name = #{realName}, role = #{role}, " +
                        "phone = #{phone}, email = #{email} WHERE id = #{id}")
        int updateUser(SysUser user);

        /**
         * 更新用户信息（含密码）
         */
        @Update("UPDATE sys_user SET real_name = #{realName}, role = #{role}, " +
                        "phone = #{phone}, email = #{email}, password = #{password} WHERE id = #{id}")
        int updateUserWithPassword(SysUser user);

        /**
         * 仅更新密码
         */
        @Update("UPDATE sys_user SET password = #{password} WHERE id = #{id}")
        int updatePassword(@Param("id") Long id, @Param("password") String password);

        /**
         * 更新用户状态
         */
        @Update("UPDATE sys_user SET status = #{status} WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") Integer status);

        /**
         * 删除用户
         */
        @Delete("DELETE FROM sys_user WHERE id = #{id}")
        int deleteById(Long id);
}