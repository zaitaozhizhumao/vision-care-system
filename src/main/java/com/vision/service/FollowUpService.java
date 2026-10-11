package com.vision.service;

import com.vision.dto.PageQuery;
import com.vision.dto.Result;
import com.vision.entity.FollowUpPlan;
import com.vision.entity.Patient;
import com.vision.entity.Prescription;
import com.vision.mapper.FollowUpMapper;
import com.vision.mapper.PatientMapper;
import com.vision.mapper.PrescriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class FollowUpService {

    @Autowired
    private FollowUpMapper followUpMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private PatientMapper patientMapper;

    /**
     * 分页查询随访计划
     */
    public Result<?> pageQuery(PageQuery query) {
        Integer offset = query.getOffset();
        List<FollowUpPlan> list = followUpMapper.findByPage(
                query.getKeyword(),
                query.getStatus(),
                offset,
                query.getLimit());
        Long count = followUpMapper.countByCondition(query.getKeyword(), query.getStatus());
        return Result.success(list, count);
    }

    /**
     * 配镜后自动生成随访计划
     */
    @Transactional
    public void generateFollowUpPlan(Long prescriptionId) {
        Prescription prescription = prescriptionMapper.findById(prescriptionId);
        if (prescription == null)
            return;

        Patient patient = patientMapper.findById(prescription.getPatientId());
        if (patient == null)
            return;

        LocalDate baseDate = LocalDate.now();
        boolean isTeenager = patient.getAge() != null && patient.getAge() < 18;

        // 一周随访
        createFollowUp(prescription.getPatientId(), prescriptionId,
                baseDate.plusWeeks(1), "ONE_WEEK",
                "配镜后一周随访，检查佩戴适应情况及镜架调整");

        // 一月随访
        createFollowUp(prescription.getPatientId(), prescriptionId,
                baseDate.plusMonths(1), "ONE_MONTH",
                "配镜后一月随访，检查视力变化及佩戴效果");

        // 三月随访
        createFollowUp(prescription.getPatientId(), prescriptionId,
                baseDate.plusMonths(3), "THREE_MONTHS",
                "配镜后三月随访，复查验光");

        // 六月随访（青少年必须）
        if (isTeenager) {
            createFollowUp(prescription.getPatientId(), prescriptionId,
                    baseDate.plusMonths(6), "SIX_MONTHS",
                    "半年随访，全面检查视力发展情况，评估近视控制效果");
        }

        // 一年随访
        createFollowUp(prescription.getPatientId(), prescriptionId,
                baseDate.plusMonths(12), "ONE_YEAR",
                "一年随访，全面眼健康检查");
    }

    private void createFollowUp(Long patientId, Long prescriptionId,
            LocalDate planDate, String type, String content) {
        FollowUpPlan plan = new FollowUpPlan();
        plan.setPatientId(patientId);
        plan.setPrescriptionId(prescriptionId);
        plan.setPlanDate(planDate);
        plan.setFollowType(type);
        plan.setContent(content);
        followUpMapper.insert(plan);
    }

    /**
     * 定时任务：每天检查待随访并记录提醒
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkAndRemindFollowUps() {
        List<FollowUpPlan> pendingList = followUpMapper.findPendingFollowUps();
        for (FollowUpPlan plan : pendingList) {
            System.out.println("[随访提醒] 患者：" + plan.getPatientName() +
                    "，电话：" + plan.getPatientPhone() +
                    "，计划日期：" + plan.getPlanDate() +
                    "，内容：" + plan.getContent());
            followUpMapper.incrementReminderCount(plan.getId());
        }
    }

    /**
     * 获取待随访列表
     */
    public List<FollowUpPlan> getPendingFollowUps() {
        try {
            return followUpMapper.findPendingFollowUps();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 根据患者ID查询随访计划
     */
    public List<FollowUpPlan> findByPatientId(Long patientId) {
        try {
            return followUpMapper.findByPatientId(patientId);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 完成随访
     */
    @Transactional
    public Result<?> completeFollowUp(Long id, String followResult) {
        int result = followUpMapper.completeFollowUp(id, "COMPLETED", followResult);
        return result > 0 ? Result.success("随访完成") : Result.error("操作失败");
    }

    /**
     * 取消随访
     */
    @Transactional
    public Result<?> cancelFollowUp(Long id) {
        int result = followUpMapper.cancelFollowUp(id);
        return result > 0 ? Result.success("取消成功") : Result.error("操作失败");
    }

    /**
     * 获取今日待随访数量
     */
    public Long countTodayFollowUps() {
        try {
            Long count = followUpMapper.countTodayFollowUps();
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 获取待随访总数
     */
    public Long countPending() {
        try {
            Long count = followUpMapper.countPending();
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 根据ID查询
     */
    public FollowUpPlan findById(Long id) {
        return followUpMapper.findById(id);
    }
}