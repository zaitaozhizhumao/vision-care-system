package com.vision.controller;

import com.vision.dto.PageQuery;
import com.vision.dto.Result;
import com.vision.entity.Prescription;
import com.vision.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 处方管理控制器
 */
@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    /**
     * 分页查询处方
     * GET /api/prescription/page?page=1&limit=15&keyword=&status=
     */
    @GetMapping("/page")
    public Result<?> page(PageQuery query) {
        return prescriptionService.pageQuery(query);
    }

    /**
     * 根据ID查询处方
     * GET /api/prescription/1
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Prescription prescription = prescriptionService.findById(id);
        if (prescription != null) {
            return Result.success(prescription);
        } else {
            return Result.error("处方不存在");
        }
    }

    /**
     * 根据患者ID查询处方
     * GET /api/prescription/patient/1
     */
    @GetMapping("/patient/{patientId}")
    public Result<?> getByPatientId(@PathVariable Long patientId) {
        List<Prescription> list = prescriptionService.findByPatientId(patientId);
        return Result.success(list);
    }

    /**
     * 从验光记录创建处方草稿
     * GET /api/prescription/create/1
     * 返回验光数据，供前端填充处方表单
     */
    @GetMapping("/create/{optometryId}")
    public Result<?> createFromOptometry(@PathVariable Long optometryId) {
        Prescription prescription = prescriptionService.createFromOptometry(optometryId);
        if (prescription != null) {
            return Result.success(prescription);
        } else {
            return Result.error("验光记录不存在");
        }
    }

    /**
     * 保存处方（新增）
     * POST /api/prescription/save
     */
    @PostMapping("/save")
    public Result<?> save(@RequestBody Prescription prescription, HttpSession session) {
        // 如果没有传入医生ID，从session获取
        if (prescription.getDoctorId() == null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                prescription.setDoctorId(userId);
            }
        }
        // 默认医生ID
        if (prescription.getDoctorId() == null) {
            prescription.setDoctorId(4L);
        }

        System.out.println("========== 保存处方 ==========");
        System.out.println("处方ID: " + (prescription.getId() != null ? prescription.getId() : "新增"));
        System.out.println("患者ID: " + prescription.getPatientId());
        System.out.println("医生ID: " + prescription.getDoctorId());
        System.out.println("==============================");

        return prescriptionService.save(prescription);
    }

    /**
     * 更新处方（仅草稿状态可更新）
     * PUT /api/prescription/update
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody Prescription prescription) {
        System.out.println("========== 更新处方 ==========");
        System.out.println("处方ID: " + prescription.getId());
        System.out.println("镜架类型: " + prescription.getFrameType());
        System.out.println("镜片类型: " + prescription.getLensType());
        System.out.println("价格: " + prescription.getPrice());
        System.out.println("==============================");
        return prescriptionService.update(prescription);
    }

    /**
     * 确认处方
     * PUT /api/prescription/confirm/1
     * 确认后自动生成随访计划
     */
    @PutMapping("/confirm/{id}")
    public Result<?> confirm(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("========== 确认处方 ==========");
        System.out.println("处方ID: " + id);
        System.out.println("==============================");
        return prescriptionService.confirm(id, userId);
    }

    /**
     * 作废处方
     * PUT /api/prescription/cancel/1
     */
    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        System.out.println("========== 作废处方 ==========");
        System.out.println("处方ID: " + id);
        System.out.println("==============================");
        return prescriptionService.cancel(id);
    }

    /**
     * 更新处方状态
     * PUT /api/prescription/status/1
     * Body: {"status": "COMPLETED"}
     */
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String status = params.get("status");
        System.out.println("========== 更新处方状态 ==========");
        System.out.println("处方ID: " + id + ", 新状态: " + status);
        System.out.println("==================================");
        return prescriptionService.updateStatus(id, status);
    }
}