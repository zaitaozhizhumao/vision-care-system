package com.vision.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 随访计划实体类
 */
@Data
public class FollowUpPlan {
    private Long id;
    private Long patientId;
    private Long prescriptionId;
    private LocalDate planDate;
    private String followType;
    private String content;
    private String status;
    private LocalDateTime completeDate;
    private String followResult;
    private Integer reminderCount;
    private LocalDateTime createTime;

    // 关联字段（用于列表展示，不存入数据库）
    private String patientName;
    private String patientPhone;

    // 判断是否逾期
    public boolean isOverdue() {
        return "PENDING".equals(status) && planDate != null && planDate.isBefore(LocalDate.now());
    }

    // 判断是否今日待办
    public boolean isToday() {
        return "PENDING".equals(status) && planDate != null && planDate.equals(LocalDate.now());
    }
}