package com.vision.service;

import com.vision.dto.LoginDTO;
import com.vision.dto.Result;
import com.vision.entity.SysUser;
import com.vision.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     */
    public Result<?> login(LoginDTO loginDTO, HttpSession session) {
        SysUser user = userMapper.findByUsername(loginDTO.getUsername());

        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("realName", user.getRealName());
        session.setAttribute("role", user.getRole());
        session.setMaxInactiveInterval(30 * 60);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("menus", getMenusByRole(user.getRole()));

        return Result.success("登录成功", data);
    }

    /**
     * 根据角色获取菜单
     */
    private List<Map<String, Object>> getMenusByRole(String role) {
        List<Map<String, Object>> menus = new ArrayList<>();
        menus.add(createMenuItem("工作台", "/vision/dashboard", "layui-icon-home"));

        switch (role) {
            case "ADMIN":
                menus.add(createMenuItem("患者管理", "/vision/patient", "layui-icon-user"));
                menus.add(createMenuItem("验光管理", "/vision/optometry", "layui-icon-survey"));
                menus.add(createMenuItem("处方管理", "/vision/prescription", "layui-icon-form"));
                menus.add(createMenuItem("加工追溯", "/vision/processing", "layui-icon-component"));
                menus.add(createMenuItem("随访管理", "/vision/followup", "layui-icon-notice"));
                menus.add(createMenuItem("数据分析", "/vision/analysis", "layui-icon-chart"));
                menus.add(createMenuItem("系统管理", "/vision/system", "layui-icon-set"));
                break;
            case "OPTOMETRIST":
                menus.add(createMenuItem("患者管理", "/vision/patient", "layui-icon-user"));
                menus.add(createMenuItem("验光管理", "/vision/optometry", "layui-icon-survey"));
                menus.add(createMenuItem("数据分析", "/vision/analysis", "layui-icon-chart"));
                break;
            case "DOCTOR":
                menus.add(createMenuItem("患者管理", "/vision/patient", "layui-icon-user"));
                menus.add(createMenuItem("验光管理", "/vision/optometry", "layui-icon-survey"));
                menus.add(createMenuItem("处方管理", "/vision/prescription", "layui-icon-form"));
                menus.add(createMenuItem("数据分析", "/vision/analysis", "layui-icon-chart"));
                break;
            case "PROCESSOR":
                menus.add(createMenuItem("加工追溯", "/vision/processing", "layui-icon-component"));
                break;
        }
        return menus;
    }

    private Map<String, Object> createMenuItem(String title, String url, String icon) {
        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("url", url);
        item.put("icon", icon);
        return item;
    }

    /**
     * 退出登录
     */
    public Result<?> logout(HttpSession session) {
        session.invalidate();
        return Result.success("退出成功");
    }

    /**
     * 获取当前用户
     */
    public SysUser getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return null;
        return userMapper.findById(userId);
    }

    /**
     * 根据ID查询用户
     */
    public SysUser findById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 根据角色查询用户
     */
    public Result<?> findByRole(String role) {
        return Result.success(userMapper.findByRole(role));
    }

    // ========== 管理员功能：用户管理 ==========

    /**
     * 获取所有用户列表
     */
    public Result<?> findAllUsers() {
        try {
            List<SysUser> list = userMapper.findAllUsers();
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 新增用户
     */
    @Transactional
    public Result<?> addUser(SysUser user) {
        try {
            SysUser existing = userMapper.findByUsername(user.getUsername());
            if (existing != null) {
                return Result.error("用户名已存在");
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode("123456"));
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            user.setStatus(1);
            int result = userMapper.insert(user);
            return result > 0 ? Result.success("添加成功") : Result.error("添加失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public Result<?> updateUser(SysUser user) {
        try {
            SysUser existing = userMapper.findById(user.getId());
            if (existing == null) {
                return Result.error("用户不存在");
            }

            // 如果传入了新密码，则加密后更新
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                userMapper.updateUserWithPassword(user);
            } else {
                // 不修改密码
                userMapper.updateUser(user);
            }

            return Result.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户状态（启用/禁用）
     */
    @Transactional
    public Result<?> updateStatus(Long id, Integer status) {
        try {
            SysUser user = userMapper.findById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }

            int result = userMapper.updateStatus(id, status);
            return result > 0 ? Result.success("状态更新成功") : Result.error("状态更新失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 重置用户密码为123456
     */
    @Transactional
    public Result<?> resetPassword(Long id) {
        try {
            SysUser user = userMapper.findById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }

            String newPassword = passwordEncoder.encode("123456");
            int result = userMapper.updatePassword(id, newPassword);
            return result > 0 ? Result.success("密码已重置为123456") : Result.error("重置失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @Transactional
    public Result<?> deleteUser(Long id) {
        try {
            SysUser user = userMapper.findById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            if ("ADMIN".equals(user.getRole())) {
                return Result.error("不能删除管理员账号");
            }

            int result = userMapper.deleteById(id);
            return result > 0 ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败: " + e.getMessage());
        }
    }
}