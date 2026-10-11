package com.vision.controller;

import com.vision.dto.PageQuery;
import com.vision.dto.Result;
import com.vision.entity.ProcessingOrder;
import com.vision.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/processing")
public class ProcessingController {

    @Autowired
    private ProcessingService processingService;

    /**
     * 分页查询加工单
     */
    @GetMapping("/page")
    public Result<?> page(PageQuery query) {
        return processingService.pageQuery(query);
    }

    /**
     * 根据ID查询加工单
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        ProcessingOrder order = processingService.findById(id);
        return order != null ? Result.success(order) : Result.error("加工单不存在");
    }

    /**
     * 从处方创建加工单
     */
    @PostMapping("/create/{prescriptionId}")
    public Result<?> create(@PathVariable Long prescriptionId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return processingService.createFromPrescription(prescriptionId, userId);
    }

    /**
     * 开始加工
     */
    @PutMapping("/start/{id}")
    public Result<?> startProcessing(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return processingService.startProcessing(id, userId);
    }

    /**
     * 完成加工
     */
    @PutMapping("/complete/{id}")
    public Result<?> completeProcessing(@PathVariable Long id) {
        return processingService.completeProcessing(id);
    }

    /**
     * 质检
     */
    @PutMapping("/quality/{id}")
    public Result<?> qualityCheck(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String result = params.get("result");
        String remark = params.get("remark");
        return processingService.qualityCheck(id, result, remark);
    }

    /**
     * 更新状态（用于交付）
     */
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String status = params.get("status");
        return processingService.updateStatus(id, status);
    }
}