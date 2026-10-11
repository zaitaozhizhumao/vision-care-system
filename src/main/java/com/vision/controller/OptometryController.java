package com.vision.controller;

import com.vision.dto.PageQuery;
import com.vision.dto.Result;
import com.vision.entity.OptometryRecord;
import com.vision.service.OptometryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 验光管理控制器
 */
@RestController
@RequestMapping("/api/optometry")
public class OptometryController {

    @Autowired
    private OptometryService optometryService;

    /**
     * 分页查询验光记录
     * GET /api/optometry/page?page=1&limit=15&patientId=&keyword=
     */
    @GetMapping("/page")
    public Result<?> page(@RequestParam(required = false) Long patientId,
            @NonNull PageQuery query) {
        return optometryService.pageQuery(patientId, query);
    }

    /**
     * 根据患者ID查询验光记录
     * GET /api/optometry/patient/1
     */
    @GetMapping("/patient/{patientId}")
    public Result<?> getByPatientId(@PathVariable Long patientId) {
        List<OptometryRecord> list = optometryService.findByPatientId(patientId);
        return Result.success(list);
    }

    /**
     * 根据ID查询验光记录
     * GET /api/optometry/1
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        OptometryRecord record = optometryService.findById(id);
        if (record != null) {
            return Result.success(record);
        } else {
            return Result.error("验光记录不存在");
        }
    }

    /**
     * 保存验光记录（新增或更新）
     * POST /api/optometry/save
     */
    @PostMapping("/save")
    public Result<?> save(@RequestBody OptometryRecord record, HttpSession session) {
        if (record.getOptometristId() == null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                record.setOptometristId(userId);
            }
        }
        if (record.getOptometristId() == null) {
            record.setOptometristId(2L);
        }

        System.out.println("========== 保存验光记录 ==========");
        System.out.println("记录ID: " + (record.getId() != null ? record.getId() : "新增"));
        System.out.println("患者ID: " + record.getPatientId());
        System.out.println("验光师ID: " + record.getOptometristId());
        System.out.println("检查时间: " + record.getCheckDate());
        System.out.println("==================================");

        return optometryService.save(record);
    }

    /**
     * 确认验光记录
     * PUT /api/optometry/confirm/10
     */
    @PutMapping("/confirm/{id}")
    public Result<?> confirm(@PathVariable Long id) {
        System.out.println("========== 确认验光记录 ==========");
        System.out.println("记录ID: " + id);
        System.out.println("==================================");
        return optometryService.confirm(id);
    }

    /**
     * 作废验光记录
     * PUT /api/optometry/cancel/10
     */
    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        System.out.println("========== 作废验光记录 ==========");
        System.out.println("记录ID: " + id);
        System.out.println("==================================");
        return optometryService.cancel(id);
    }

    /**
     * 获取患者视力分析数据
     * GET /api/optometry/analysis/1
     */
    @GetMapping("/analysis/{patientId}")
    public Result<?> getAnalysis(@PathVariable Long patientId) {
        Map<String, Object> data = optometryService.getVisionAnalysis(patientId);
        return Result.success(data);
    }
}