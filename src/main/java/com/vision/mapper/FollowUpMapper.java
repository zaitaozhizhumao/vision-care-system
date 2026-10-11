package com.vision.mapper;

import com.vision.entity.FollowUpPlan;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 随访计划 Mapper
 */
@Mapper
public interface FollowUpMapper {

        /**
         * 根据ID查询随访计划
         */
        @Select("SELECT f.*, p.name as patient_name, p.phone as patient_phone " +
                        "FROM follow_up_plan f " +
                        "LEFT JOIN patient p ON f.patient_id = p.id " +
                        "WHERE f.id = #{id}")
        FollowUpPlan findById(Long id);

        /**
         * 查询待随访列表（计划日期小于等于今天且状态为待随访）
         */
        @Select("SELECT f.*, p.name as patient_name, p.phone as patient_phone " +
                        "FROM follow_up_plan f " +
                        "LEFT JOIN patient p ON f.patient_id = p.id " +
                        "WHERE f.status = 'PENDING' AND f.plan_date <= CURDATE() " +
                        "ORDER BY f.plan_date ASC")
        List<FollowUpPlan> findPendingFollowUps();

        /**
         * 根据患者ID查询随访计划
         */
        @Select("SELECT f.*, p.name as patient_name, p.phone as patient_phone " +
                        "FROM follow_up_plan f " +
                        "LEFT JOIN patient p ON f.patient_id = p.id " +
                        "WHERE f.patient_id = #{patientId} " +
                        "ORDER BY f.plan_date DESC")
        List<FollowUpPlan> findByPatientId(@Param("patientId") Long patientId);

        /**
         * 根据处方ID查询随访计划
         */
        @Select("SELECT f.*, p.name as patient_name, p.phone as patient_phone " +
                        "FROM follow_up_plan f " +
                        "LEFT JOIN patient p ON f.patient_id = p.id " +
                        "WHERE f.prescription_id = #{prescriptionId} " +
                        "ORDER BY f.plan_date ASC")
        List<FollowUpPlan> findByPrescriptionId(@Param("prescriptionId") Long prescriptionId);

        /**
         * 分页查询随访计划
         */
        List<FollowUpPlan> findByPage(@Param("keyword") String keyword,
                        @Param("status") String status,
                        @Param("offset") Integer offset,
                        @Param("limit") Integer limit);

        /**
         * 统计符合条件的随访计划数量
         */
        Long countByCondition(@Param("keyword") String keyword,
                        @Param("status") String status);

        /**
         * 新增随访计划
         */
        @Insert("INSERT INTO follow_up_plan(patient_id, prescription_id, plan_date, follow_type, content, status, reminder_count) "
                        +
                        "VALUES(#{patientId}, #{prescriptionId}, #{planDate}, #{followType}, #{content}, 'PENDING', 0)")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(FollowUpPlan plan);

        /**
         * 完成随访
         */
        @Update("UPDATE follow_up_plan SET status = #{status}, complete_date = NOW(), " +
                        "follow_result = #{followResult} WHERE id = #{id}")
        int completeFollowUp(@Param("id") Long id,
                        @Param("status") String status,
                        @Param("followResult") String followResult);

        /**
         * 增加提醒次数
         */
        @Update("UPDATE follow_up_plan SET reminder_count = reminder_count + 1 WHERE id = #{id}")
        int incrementReminderCount(Long id);

        /**
         * 取消随访计划
         */
        @Update("UPDATE follow_up_plan SET status = 'CANCELLED' WHERE id = #{id}")
        int cancelFollowUp(Long id);

        /**
         * 标记为逾期
         */
        @Update("UPDATE follow_up_plan SET status = 'MISSED' WHERE id = #{id}")
        int markAsMissed(Long id);

        /**
         * 统计今日待随访数量
         */
        @Select("SELECT COUNT(*) FROM follow_up_plan WHERE status = 'PENDING' AND plan_date <= CURDATE()")
        Long countTodayFollowUps();

        /**
         * 统计待随访总数
         */
        @Select("SELECT COUNT(*) FROM follow_up_plan WHERE status = 'PENDING'")
        Long countPending();

        /**
         * 统计已逾期数量
         */
        @Select("SELECT COUNT(*) FROM follow_up_plan WHERE status = 'MISSED'")
        Long countMissed();

        /**
         * 统计已完成数量
         */
        @Select("SELECT COUNT(*) FROM follow_up_plan WHERE status = 'COMPLETED'")
        Long countCompleted();

        /**
         * 根据患者ID统计待随访数量
         */
        @Select("SELECT COUNT(*) FROM follow_up_plan WHERE patient_id = #{patientId} AND status = 'PENDING'")
        Long countPendingByPatientId(@Param("patientId") Long patientId);

        /**
         * 删除随访计划（物理删除，慎用）
         */
        @Delete("DELETE FROM follow_up_plan WHERE id = #{id}")
        int deleteById(Long id);

        /**
         * 批量生成随访计划
         */
        @Insert("<script>" +
                        "INSERT INTO follow_up_plan(patient_id, prescription_id, plan_date, follow_type, content, status) VALUES "
                        +
                        "<foreach collection='list' item='plan' separator=','>" +
                        "(#{plan.patientId}, #{plan.prescriptionId}, #{plan.planDate}, #{plan.followType}, #{plan.content}, 'PENDING')"
                        +
                        "</foreach>" +
                        "</script>")
        int batchInsert(@Param("list") List<FollowUpPlan> plans);
}