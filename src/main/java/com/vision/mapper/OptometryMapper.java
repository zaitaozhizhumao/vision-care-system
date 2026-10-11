package com.vision.mapper;

import com.vision.dto.VisionAnalysisDTO;
import com.vision.entity.OptometryRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OptometryMapper {

        @Select("SELECT o.*, p.name as patient_name, u.real_name as optometrist_name " +
                        "FROM optometry_record o " +
                        "LEFT JOIN patient p ON o.patient_id = p.id " +
                        "LEFT JOIN sys_user u ON o.optometrist_id = u.id " +
                        "WHERE o.id = #{id}")
        OptometryRecord findById(Long id);

        @Select("SELECT o.*, p.name as patient_name, u.real_name as optometrist_name " +
                        "FROM optometry_record o " +
                        "LEFT JOIN patient p ON o.patient_id = p.id " +
                        "LEFT JOIN sys_user u ON o.optometrist_id = u.id " +
                        "WHERE o.patient_id = #{patientId} " +
                        "ORDER BY o.check_date DESC")
        List<OptometryRecord> findByPatientId(@Param("patientId") Long patientId);

        List<OptometryRecord> findByPage(@Param("patientId") Long patientId,
                        @Param("keyword") String keyword,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);

        Long countByCondition(@Param("patientId") Long patientId,
                        @Param("keyword") String keyword);

        @Insert("INSERT INTO optometry_record(patient_id, optometrist_id, check_date, " +
                        "vision_od_naked, vision_os_naked, vision_ou_naked, " +
                        "sphere_od, sphere_os, cylinder_od, cylinder_os, axis_od, axis_os, add_od, add_os, " +
                        "vision_od_corrected, vision_os_corrected, vision_ou_corrected, " +
                        "pd_distance, ph_distance, eye_pressure_od, eye_pressure_os, remark, status) " +
                        "VALUES(#{patientId}, #{optometristId}, #{checkDate}, " +
                        "#{visionOdNaked}, #{visionOsNaked}, #{visionOuNaked}, " +
                        "#{sphereOd}, #{sphereOs}, #{cylinderOd}, #{cylinderOs}, #{axisOd}, #{axisOs}, " +
                        "#{addOd}, #{addOs}, " +
                        "#{visionOdCorrected}, #{visionOsCorrected}, #{visionOuCorrected}, " +
                        "#{pdDistance}, #{phDistance}, #{eyePressureOd}, #{eyePressureOs}, #{remark}, #{status})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(OptometryRecord record);

        /**
         * 更新验光记录（完整更新）
         */
        @Update("UPDATE optometry_record SET " +
                        "patient_id = #{patientId}, " +
                        "optometrist_id = #{optometristId}, " +
                        "check_date = #{checkDate}, " +
                        "vision_od_naked = #{visionOdNaked}, " +
                        "vision_os_naked = #{visionOsNaked}, " +
                        "vision_ou_naked = #{visionOuNaked}, " +
                        "sphere_od = #{sphereOd}, " +
                        "sphere_os = #{sphereOs}, " +
                        "cylinder_od = #{cylinderOd}, " +
                        "cylinder_os = #{cylinderOs}, " +
                        "axis_od = #{axisOd}, " +
                        "axis_os = #{axisOs}, " +
                        "add_od = #{addOd}, " +
                        "add_os = #{addOs}, " +
                        "vision_od_corrected = #{visionOdCorrected}, " +
                        "vision_os_corrected = #{visionOsCorrected}, " +
                        "vision_ou_corrected = #{visionOuCorrected}, " +
                        "pd_distance = #{pdDistance}, " +
                        "ph_distance = #{phDistance}, " +
                        "eye_pressure_od = #{eyePressureOd}, " +
                        "eye_pressure_os = #{eyePressureOs}, " +
                        "remark = #{remark} " +
                        "WHERE id = #{id}")
        int update(OptometryRecord record);

        @Update("UPDATE optometry_record SET status = #{status} WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") String status);

        @Select("SELECT check_date, sphere_od, sphere_os, cylinder_od, cylinder_os, " +
                        "vision_od_corrected, vision_os_corrected " +
                        "FROM optometry_record WHERE patient_id = #{patientId} AND status = 'CONFIRMED' " +
                        "ORDER BY check_date")
        List<VisionAnalysisDTO> getVisionTrend(@Param("patientId") Long patientId);

        @Select("SELECT COUNT(*) FROM optometry_record WHERE DATE(check_date) = CURDATE()")
        Long countToday();
}