package com.vision.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 页面路由控制器
 * 负责所有页面的跳转
 */
@Controller
public class PageController {

    // =====================================================
    // 公共页面
    // =====================================================

    /**
     * 登录页
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 首页（主框架）
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 工作台/仪表盘
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // =====================================================
    // 患者管理
    // =====================================================

    /**
     * 患者列表页
     */
    @GetMapping("/patient")
    public String patientList() {
        return "patient/list";
    }

    /**
     * 新增患者页
     */
    @GetMapping("/patient/add")
    public String patientAdd() {
        return "patient/add";
    }

    /**
     * 编辑患者页（复用新增页面）
     */
    @GetMapping("/patient/edit")
    public String patientEdit(@RequestParam(required = false) Long id) {
        return "patient/add";
    }

    /**
     * 患者详情页
     */
    @GetMapping("/patient/detail")
    public String patientDetail(@RequestParam(required = false) Long id) {
        return "patient/detail";
    }

    // =====================================================
    // 验光管理
    // =====================================================

    /**
     * 验光记录列表页
     */
    @GetMapping("/optometry")
    public String optometryList() {
        return "optometry/list";
    }

    /**
     * 新建验光页
     */
    @GetMapping("/optometry/add")
    public String optometryAdd(@RequestParam(required = false) Long patientId) {
        return "optometry/add";
    }

    /**
     * 编辑验光记录页（仅草稿状态可编辑）
     */
    @GetMapping("/optometry/edit")
    public String optometryEdit(@RequestParam(required = false) Long id) {
        return "optometry/edit";
    }

    /**
     * 验光详情页
     */
    @GetMapping("/optometry/detail")
    public String optometryDetail(@RequestParam(required = false) Long id) {
        return "optometry/detail";
    }

    // =====================================================
    // 处方管理
    // =====================================================

    /**
     * 处方列表页
     */
    @GetMapping("/prescription")
    public String prescriptionList() {
        return "prescription/list";
    }

    /**
     * 开具处方页
     */
    @GetMapping("/prescription/add")
    public String prescriptionAdd(@RequestParam(required = false) Long optometryId) {
        return "prescription/add";
    }

    /**
     * 编辑处方页（仅草稿状态可编辑）
     */
    @GetMapping("/prescription/edit")
    public String prescriptionEdit(@RequestParam(required = false) Long id) {
        return "prescription/edit";
    }

    /**
     * 处方详情页
     */
    @GetMapping("/prescription/detail")
    public String prescriptionDetail(@RequestParam(required = false) Long id) {
        return "prescription/detail";
    }

    // =====================================================
    // 加工管理
    // =====================================================

    /**
     * 加工单列表页
     */
    @GetMapping("/processing")
    public String processingList() {
        return "processing/list";
    }

    /**
     * 加工单详情页
     */
    @GetMapping("/processing/detail")
    public String processingDetail(@RequestParam(required = false) Long id) {
        return "processing/detail";
    }

    // =====================================================
    // 随访管理
    // =====================================================

    /**
     * 随访计划列表页
     */
    @GetMapping("/followup")
    public String followupList() {
        return "followup/list";
    }

    // =====================================================
    // 数据分析
    // =====================================================

    /**
     * 数据分析页
     */
    @GetMapping("/analysis")
    public String analysis() {
        return "analysis/index";
    }

    // =====================================================
    // 系统管理
    // =====================================================

    /**
     * 系统管理首页
     */
    @GetMapping("/system")
    public String system() {
        return "system/index";
    }

    /**
     * 用户管理页
     */
    @GetMapping("/system/user")
    public String userManage() {
        return "system/user";
    }

    /**
     * 操作日志页
     */
    @GetMapping("/system/log")
    public String logManage() {
        return "system/log";
    }
}