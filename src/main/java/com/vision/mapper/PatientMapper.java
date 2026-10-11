package com.vision.mapper;

import com.vision.entity.Patient;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PatientMapper {

        @Select("SELECT id, patient_no, name, gender, birth_date, phone, id_card, address, " +
                        "medical_history, family_history, deleted, create_time, update_time " +
                        "FROM patient WHERE id = #{id} AND deleted = 0")
        Patient findById(Long id);

        @Select("SELECT id, patient_no, name, gender, birth_date, phone, id_card, address, " +
                        "medical_history, family_history, deleted, create_time, update_time " +
                        "FROM patient WHERE patient_no = #{patientNo} AND deleted = 0")
        Patient findByPatientNo(String patientNo);

        // 这两个方法对应 XML 中的实现
        List<Patient> findByPage(@Param("keyword") String keyword,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);

        Long countByKeyword(@Param("keyword") String keyword);

        @Insert("INSERT INTO patient(patient_no, name, gender, birth_date, phone, id_card, address, medical_history, family_history, deleted) "
                        +
                        "VALUES(#{patientNo}, #{name}, #{gender}, #{birthDate}, #{phone}, #{idCard}, #{address}, #{medicalHistory}, #{familyHistory}, 0)")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Patient patient);

        @Update("UPDATE patient SET name=#{name}, gender=#{gender}, birth_date=#{birthDate}, " +
                        "phone=#{phone}, id_card=#{idCard}, address=#{address}, medical_history=#{medicalHistory}, " +
                        "family_history=#{familyHistory} WHERE id=#{id} AND deleted = 0")
        int update(Patient patient);

        @Update("UPDATE patient SET deleted = 1 WHERE id = #{id}")
        int deleteById(Long id);

        @Select("SELECT MAX(patient_no) FROM patient WHERE patient_no LIKE CONCAT(DATE_FORMAT(NOW(), '%Y%m%d'), '%')")
        String getMaxPatientNoToday();

        @Select("SELECT COUNT(*) FROM patient WHERE deleted = 0")
        Long countAll();

        // 搜索患者（用于下拉选择）
        @Select("SELECT id, patient_no, name, gender, birth_date, phone, id_card, address " +
                        "FROM patient WHERE deleted = 0 ORDER BY create_time DESC LIMIT 50")
        List<Patient> findAllActive();
}