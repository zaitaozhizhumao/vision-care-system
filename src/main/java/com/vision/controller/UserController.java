package com.vision.controller;

import com.vision.dto.LoginDTO;
import com.vision.dto.Result;
import com.vision.entity.SysUser;
import com.vision.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        return userService.login(loginDTO, session);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        return userService.logout(session);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<?> getUserInfo(HttpSession session) {
        SysUser user = userService.getCurrentUser(session);
        if (user == null) {
            return Result.error(401, "未登录");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        return Result.success(data);
    }

    /**
     * 根据角色获取用户列表
     */
    @GetMapping("/role/{role}")
    public Result<?> getByRole(@PathVariable String role) {
        return userService.findByRole(role);
    }

    // ========== 管理员功能：用户管理 ==========

    /**
     * 获取所有用户列表
     */
    @GetMapping("/list")
    public Result<?> list() {
        return userService.findAllUsers();
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        SysUser user = userService.findById(id);
        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            data.put("role", user.getRole());
            data.put("phone", user.getPhone());
            data.put("email", user.getEmail());
            data.put("status", user.getStatus());
            return Result.success(data);
        }
        return Result.error("用户不存在");
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    public Result<?> addUser(@RequestBody SysUser user) {
        System.out.println("新增用户: " + user.getUsername() + ", 角色: " + user.getRole());
        return userService.addUser(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody SysUser user) {
        System.out.println("更新用户: id=" + user.getId() + ", password="
                + (user.getPassword() != null && !user.getPassword().isEmpty() ? "有值" : "空"));
        return userService.updateUser(user);
    }

    /**
     * 更新用户状态（启用/禁用）
     */
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        return userService.updateStatus(id, status);
    }

    /**
     * 重置用户密码为123456
     */
    @PutMapping("/password/{id}")
    public Result<?> resetPassword(@PathVariable Long id) {
        return userService.resetPassword(id);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}