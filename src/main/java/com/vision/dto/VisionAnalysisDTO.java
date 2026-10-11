package com.vision.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 视力分析DTO
 */
@Data
public class VisionAnalysisDTO {
    private LocalDateTime checkDate;
    private BigDecimal sphereOd;
    private BigDecimal sphereOs;
    private BigDecimal cylinderOd;
    private BigDecimal cylinderOs;
    private BigDecimal visionOdCorrected;
    private BigDecimal visionOsCorrected;
}