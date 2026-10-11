package com.vision.controller;

import com.vision.dto.PageQuery;
import com.vision.dto.Result;
import com.vision.entity.FollowUpPlan;
import com.vision.service.FollowUpService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 随访管理控制器
 */
@RestController
@RequestMapping("/api/followup")
public class FollowUpController {

    private final FollowUpService followUpService;

    public FollowUpController(FollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    /**
     * 分页查询随访计划
     */
    @GetMapping("/page")
    public Result<?> page(PageQuery query) {
        return followUpService.pageQuery(query);
    }

    /**
     * 获取待随访列表
     */
    @GetMapping("/pending")
    public Result<?> getPendingFollowUps() {
        List<FollowUpPlan> list = followUpService.getPendingFollowUps();
        return Result.success(list);
    }

    /**
     * 根据患者ID查询随访计划
     */
    @GetMapping("/patient/{patientId}")
    public Result<?> getByPatientId(@PathVariable Long patientId) {
        List<FollowUpPlan> list = followUpService.findByPatientId(patientId);
        return Result.success(list);
    }

    /**
     * 获取今日待随访数量
     */
    @GetMapping("/today/count")
    public Result<?> getTodayCount() {
        Long count = followUpService.countTodayFollowUps();
        Long pending = followUpService.countPending();
        Map<String, Long> result = new HashMap<>();
        result.put("today", count);
        result.put("pending", pending);
        return Result.success(result);
    }

    /**
     * 根据处方生成随访计划
     */
    @PostMapping("/generate/{prescriptionId}")
    public Result<?> generatePlan(@PathVariable Long prescriptionId) {
        followUpService.generateFollowUpPlan(prescriptionId);
        return Result.success("随访计划生成成功");
    }

    /**
     * 完成随访
     */
    @PutMapping("/complete/{id}")
    public Result<?> complete(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String followResult = params.get("followResult");
        return followUpService.completeFollowUp(id, followResult);
    }

    /**
     * 取消随访
     */
    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        return followUpService.cancelFollowUp(id);
    }
}