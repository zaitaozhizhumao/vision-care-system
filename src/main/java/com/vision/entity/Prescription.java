package com.vision.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 处方实体类
 */
@Data
public class Prescription {
    private Long id;
    private String prescriptionNo;
    private Long patientId;
    private Long optometryId;
    private Long doctorId;
    
    private String frameType;
    private String frameBrand;
    private String frameModel;
    private String lensType;
    private String lensMaterial;
    private Double lensIndex;
    private String lensCoating;
    
    private BigDecimal sphereOd;
    private BigDecimal sphereOs;
    private BigDecimal cylinderOd;
    private BigDecimal cylinderOs;
    private Integer axisOd;
    private Integer axisOs;
    private BigDecimal addOd;
    private BigDecimal addOs;
    private BigDecimal pdDistance;
    private BigDecimal phDistance;
    
    private String usageType;
    private BigDecimal price;
    private BigDecimal paidAmount;
    private String paymentStatus;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String patientName;
    private String patientPhone;
    private String doctorName;
}