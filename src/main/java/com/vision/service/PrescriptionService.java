package com.vision.service;

import com.vision.dto.PageQuery;
import com.vision.dto.Result;
import com.vision.entity.OptometryRecord;
import com.vision.entity.Prescription;
import com.vision.mapper.OptometryMapper;
import com.vision.mapper.PrescriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 处方服务类
 */
@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private OptometryMapper optometryMapper;

    @Autowired
    private FollowUpService followUpService;

    /**
     * 分页查询处方
     */
    public Result<?> pageQuery(PageQuery query) {
        try {
            Integer offset = query.getOffset();
            List<Prescription> list = prescriptionMapper.findByPage(
                    query.getKeyword(), query.getStatus(), offset, query.getLimit());
            Long count = prescriptionMapper.countByCondition(query.getKeyword(), query.getStatus());
            return Result.success(list, count);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询处方
     */
    public Prescription findById(Long id) {
        return prescriptionMapper.findById(id);
    }

    /**
     * 根据患者ID查询处方
     */
    public List<Prescription> findByPatientId(Long patientId) {
        return prescriptionMapper.findByPatientId(patientId);
    }

    /**
     * 从验光记录创建处方草稿数据
     */
    public Prescription createFromOptometry(Long optometryId) {
        OptometryRecord optometry = optometryMapper.findById(optometryId);
        if (optometry == null) {
            return null;
        }

        Prescription prescription = new Prescription();
        prescription.setPatientId(optometry.getPatientId());
        prescription.setOptometryId(optometryId);
        prescription.setSphereOd(optometry.getSphereOd());
        prescription.setSphereOs(optometry.getSphereOs());
        prescription.setCylinderOd(optometry.getCylinderOd());
        prescription.setCylinderOs(optometry.getCylinderOs());
        prescription.setAxisOd(optometry.getAxisOd());
        prescription.setAxisOs(optometry.getAxisOs());
        prescription.setAddOd(optometry.getAddOd());
        prescription.setAddOs(optometry.getAddOs());
        prescription.setPdDistance(optometry.getPdDistance());
        prescription.setPhDistance(optometry.getPhDistance());

        // 设置关联名称
        prescription.setPatientName(optometry.getPatientName());

        return prescription;
    }

    /**
     * 保存处方（新增）
     */
    @Transactional
    public Result<?> save(Prescription prescription) {
        try {
            // 生成处方编号
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String maxNo = prescriptionMapper.getMaxPrescriptionNoToday();
            int seq = 1;
            if (maxNo != null && maxNo.length() >= 13) {
                try {
                    seq = Integer.parseInt(maxNo.substring(9)) + 1;
                } catch (NumberFormatException e) {
                    seq = 1;
                }
            }
            prescription.setPrescriptionNo("P" + dateStr + String.format("%04d", seq));
            prescription.setStatus("DRAFT");
            prescription.setPaymentStatus("UNPAID");

            int result = prescriptionMapper.insert(prescription);
            if (result > 0) {
                return Result.success("创建成功", prescription);
            } else {
                return Result.error("创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 更新处方（仅草稿状态可更新）
     */
    @Transactional
    public Result<?> update(Prescription prescription) {
        try {
            if (prescription.getId() == null) {
                return Result.error("处方ID不能为空");
            }

            // 查询原处方
            Prescription existing = prescriptionMapper.findById(prescription.getId());
            if (existing == null) {
                return Result.error("处方不存在");
            }

            // 检查状态
            if (!"DRAFT".equals(existing.getStatus())) {
                return Result.error("只有草稿状态的处方可以编辑");
            }

            // 保持原有信息不变
            prescription.setPrescriptionNo(existing.getPrescriptionNo());
            prescription.setPatientId(existing.getPatientId());
            prescription.setOptometryId(existing.getOptometryId());
            prescription.setStatus("DRAFT");

            int result = prescriptionMapper.update(prescription);
            if (result > 0) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 确认处方（生成随访计划）
     */
    @Transactional
    public Result<?> confirm(Long id, Long doctorId) {
        try {
            Prescription prescription = prescriptionMapper.findById(id);
            if (prescription == null) {
                return Result.error("处方不存在");
            }
            if (!"DRAFT".equals(prescription.getStatus())) {
                return Result.error("只有草稿状态的处方可以确认");
            }

            // 更新状态为待处理
            prescriptionMapper.updateStatus(id, "PENDING");

            // 自动生成随访计划
            followUpService.generateFollowUpPlan(id);

            return Result.success("处方已确认，随访计划已生成");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("确认失败: " + e.getMessage());
        }
    }

    /**
     * 作废处方
     */
    @Transactional
    public Result<?> cancel(Long id) {
        try {
            Prescription prescription = prescriptionMapper.findById(id);
            if (prescription == null) {
                return Result.error("处方不存在");
            }
            if ("CANCELLED".equals(prescription.getStatus())) {
                return Result.error("该处方已经作废");
            }

            int result = prescriptionMapper.updateStatus(id, "CANCELLED");
            if (result > 0) {
                return Result.success("作废成功");
            } else {
                return Result.error("作废失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("作废失败: " + e.getMessage());
        }
    }

    /**
     * 更新处方状态
     */
    @Transactional
    public Result<?> updateStatus(Long id, String status) {
        try {
            Prescription prescription = prescriptionMapper.findById(id);
            if (prescription == null) {
                return Result.error("处方不存在");
            }

            int result = prescriptionMapper.updateStatus(id, status);
            if (result > 0) {
                return Result.success("状态更新成功");
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }
}