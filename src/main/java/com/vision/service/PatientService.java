package com.vision.service;

import com.vision.dto.PageQuery;
import com.vision.dto.Result;
import com.vision.entity.Patient;
import com.vision.mapper.PatientMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PatientService {

    private final PatientMapper patientMapper;

    public PatientService(PatientMapper patientMapper) {
        this.patientMapper = patientMapper;
    }

    /**
     * 分页查询患者
     */
    public Result<?> pageQuery(PageQuery query) {
        try {
            Integer offset = (query.getPage() - 1) * query.getLimit();
            List<Patient> list = patientMapper.findByPage(query.getKeyword(), offset, query.getLimit());
            Long count = patientMapper.countByKeyword(query.getKeyword());
            return Result.success(list, count);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询患者
     */
    public Patient findById(Long id) {
        return patientMapper.findById(id);
    }

    /**
     * 保存患者
     */
    @Transactional
    public Result<?> save(Patient patient) {
        try {
            if (patient.getName() == null || patient.getName().trim().isEmpty()) {
                return Result.error("患者姓名不能为空");
            }
            if (patient.getPhone() == null || patient.getPhone().trim().isEmpty()) {
                return Result.error("联系电话不能为空");
            }

            if (patient.getId() == null) {
                // 新增
                String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                String maxNo = patientMapper.getMaxPatientNoToday();
                int seq = 1;
                if (maxNo != null && maxNo.length() >= 12) {
                    try {
                        seq = Integer.parseInt(maxNo.substring(8)) + 1;
                    } catch (NumberFormatException e) {
                        seq = 1;
                    }
                }
                patient.setPatientNo(dateStr + String.format("%04d", seq));

                int result = patientMapper.insert(patient);
                return result > 0 ? Result.success("添加成功", patient) : Result.error("添加失败");
            } else {
                // 更新
                Patient existing = patientMapper.findById(patient.getId());
                if (existing == null) {
                    return Result.error("患者不存在");
                }
                patient.setPatientNo(existing.getPatientNo());

                int result = patientMapper.update(patient);
                return result > 0 ? Result.success("更新成功", patient) : Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 删除患者
     */
    @Transactional
    public Result<?> delete(Long id) {
        try {
            Patient patient = patientMapper.findById(id);
            if (patient == null) {
                return Result.error("患者不存在");
            }

            int result = patientMapper.deleteById(id);
            return result > 0 ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 搜索患者
     */
    public List<Patient> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return patientMapper.findAllActive();
        }
        return patientMapper.findByPage(keyword, 0, 50);
    }
}