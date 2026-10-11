package com.vision.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 加工单实体类
 */
@Data
public class ProcessingOrder {
    private Long id;
    private String orderNo;
    private Long prescriptionId;
    private Long processorId;
    
    private String frameModel;
    private String frameBrand;
    private String frameColor;
    private String lensType;
    private Double lensIndex;
    private String lensDesign;
    
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private String qualityCheckResult;
    private String qualityCheckRemark;
    private String remark;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String patientName;
    private String prescriptionNo;
    private String processorName;
}