package com.vision.mapper;

import com.vision.entity.Prescription;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 处方 Mapper
 */
@Mapper
public interface PrescriptionMapper {

        /**
         * 根据ID查询处方（含关联信息）
         */
        @Select("SELECT p.*, pt.name as patient_name, pt.phone as patient_phone, u.real_name as doctor_name " +
                        "FROM prescription p " +
                        "LEFT JOIN patient pt ON p.patient_id = pt.id " +
                        "LEFT JOIN sys_user u ON p.doctor_id = u.id " +
                        "WHERE p.id = #{id}")
        Prescription findById(Long id);

        /**
         * 根据患者ID查询处方
         */
        @Select("SELECT p.*, pt.name as patient_name, u.real_name as doctor_name " +
                        "FROM prescription p " +
                        "LEFT JOIN patient pt ON p.patient_id = pt.id " +
                        "LEFT JOIN sys_user u ON p.doctor_id = u.id " +
                        "WHERE p.patient_id = #{patientId} " +
                        "ORDER BY p.create_time DESC")
        List<Prescription> findByPatientId(@Param("patientId") Long patientId);

        /**
         * 分页查询处方
         */
        List<Prescription> findByPage(@Param("keyword") String keyword,
                        @Param("status") String status,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);

        /**
         * 统计处方数量
         */
        Long countByCondition(@Param("keyword") String keyword, @Param("status") String status);

        /**
         * 新增处方
         */
        @Insert("INSERT INTO prescription(prescription_no, patient_id, optometry_id, doctor_id, " +
                        "frame_type, frame_brand, frame_model, lens_type, lens_material, lens_index, lens_coating, " +
                        "sphere_od, sphere_os, cylinder_od, cylinder_os, axis_od, axis_os, add_od, add_os, " +
                        "pd_distance, ph_distance, usage_type, price, paid_amount, payment_status, status) " +
                        "VALUES(#{prescriptionNo}, #{patientId}, #{optometryId}, #{doctorId}, " +
                        "#{frameType}, #{frameBrand}, #{frameModel}, #{lensType}, #{lensMaterial}, #{lensIndex}, #{lensCoating}, "
                        +
                        "#{sphereOd}, #{sphereOs}, #{cylinderOd}, #{cylinderOs}, #{axisOd}, #{axisOs}, #{addOd}, #{addOs}, "
                        +
                        "#{pdDistance}, #{phDistance}, #{usageType}, #{price}, #{paidAmount}, #{paymentStatus}, #{status})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Prescription prescription);

        /**
         * 更新处方（镜架镜片信息、费用等）
         */
        @Update("UPDATE prescription SET " +
                        "frame_type = #{frameType}, " +
                        "frame_brand = #{frameBrand}, " +
                        "frame_model = #{frameModel}, " +
                        "lens_type = #{lensType}, " +
                        "lens_material = #{lensMaterial}, " +
                        "lens_index = #{lensIndex}, " +
                        "lens_coating = #{lensCoating}, " +
                        "sphere_od = #{sphereOd}, " +
                        "sphere_os = #{sphereOs}, " +
                        "cylinder_od = #{cylinderOd}, " +
                        "cylinder_os = #{cylinderOs}, " +
                        "axis_od = #{axisOd}, " +
                        "axis_os = #{axisOs}, " +
                        "add_od = #{addOd}, " +
                        "add_os = #{addOs}, " +
                        "pd_distance = #{pdDistance}, " +
                        "ph_distance = #{phDistance}, " +
                        "usage_type = #{usageType}, " +
                        "price = #{price} " +
                        "WHERE id = #{id}")
        int update(Prescription prescription);

        /**
         * 更新处方状态
         */
        @Update("UPDATE prescription SET status = #{status} WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") String status);

        /**
         * 获取当天最大处方编号
         */
        @Select("SELECT MAX(prescription_no) FROM prescription WHERE prescription_no LIKE CONCAT('P', DATE_FORMAT(NOW(), '%Y%m%d'), '%')")
        String getMaxPrescriptionNoToday();
}