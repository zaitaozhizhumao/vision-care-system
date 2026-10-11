package com.vision.controller;

import com.vision.dto.PageQuery;
import com.vision.dto.Result;
import com.vision.entity.Patient;
import com.vision.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * 分页查询患者
     */
    @GetMapping("/page")
    public Result<?> page(PageQuery query) {
        return patientService.pageQuery(query);
    }

    /**
     * 搜索患者（用于下拉选择）
     */
    @GetMapping("/search")
    public Result<?> search(@RequestParam(required = false) String keyword) {
        return Result.success(patientService.search(keyword));
    }

    /**
     * 根据ID查询患者
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        try {
            Patient patient = patientService.findById(id);
            if (patient != null) {
                return Result.success(patient);
            } else {
                return Result.error("患者不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 保存患者
     */
    @PostMapping("/save")
    public Result<?> save(@RequestBody Patient patient) {
        return patientService.save(patient);
    }

    /**
     * 删除患者
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            return patientService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}