package com.vision.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OptometryRecord {
    private Long id;
    private Long patientId;
    private Long optometristId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime checkDate;

    // 裸眼视力
    private BigDecimal visionOdNaked;
    private BigDecimal visionOsNaked;
    private BigDecimal visionOuNaked;

    // 电脑验光
    private BigDecimal sphereOd;
    private BigDecimal sphereOs;
    private BigDecimal cylinderOd;
    private BigDecimal cylinderOs;
    private Integer axisOd;
    private Integer axisOs;
    private BigDecimal addOd;
    private BigDecimal addOs;

    // 矫正视力
    private BigDecimal visionOdCorrected;
    private BigDecimal visionOsCorrected;
    private BigDecimal visionOuCorrected;

    // 其他参数
    private BigDecimal pdDistance;
    private BigDecimal phDistance;
    private BigDecimal eyePressureOd;
    private BigDecimal eyePressureOs;

    private String remark;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 关联字段（用于列表展示，不存入数据库）
    private String patientName;
    private String optometristName;
}