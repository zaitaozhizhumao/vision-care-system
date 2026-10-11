package com.vision.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者实体类
 */
@Data
public class Patient {
    private Long id;
    private String patientNo;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String phone;
    private String idCard;
    private String address;
    private String medicalHistory;
    private String familyHistory;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 用于显示的年龄
    public Integer getAge() {
        if (birthDate == null)
            return null;
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}