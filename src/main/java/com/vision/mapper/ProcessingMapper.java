package com.vision.mapper;

import com.vision.entity.ProcessingOrder;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 加工单Mapper
 */
@Mapper
public interface ProcessingMapper {
    
    @Select("SELECT po.*, pt.name as patient_name, p.prescription_no, u.real_name as processor_name " +
            "FROM processing_order po " +
            "LEFT JOIN prescription p ON po.prescription_id = p.id " +
            "LEFT JOIN patient pt ON p.patient_id = pt.id " +
            "LEFT JOIN sys_user u ON po.processor_id = u.id " +
            "WHERE po.id = #{id}")
    ProcessingOrder findById(Long id);
    
    @Select("SELECT po.*, pt.name as patient_name, p.prescription_no " +
            "FROM processing_order po " +
            "LEFT JOIN prescription p ON po.prescription_id = p.id " +
            "LEFT JOIN patient pt ON p.patient_id = pt.id " +
            "WHERE po.status = #{status}")
    List<ProcessingOrder> findByStatus(@Param("status") String status);
    
    List<ProcessingOrder> findByPage(@Param("keyword") String keyword,
                                     @Param("status") String status,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);
    
    Long countByCondition(@Param("keyword") String keyword, @Param("status") String status);
    
    @Insert("INSERT INTO processing_order(order_no, prescription_id, processor_id, " +
            "frame_model, frame_brand, frame_color, lens_type, lens_index, lens_design, " +
            "status, remark) VALUES(#{orderNo}, #{prescriptionId}, #{processorId}, " +
            "#{frameModel}, #{frameBrand}, #{frameColor}, #{lensType}, #{lensIndex}, #{lensDesign}, " +
            "#{status}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProcessingOrder order);
    
    @Update("UPDATE processing_order SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Update("UPDATE processing_order SET status='PROCESSING', start_time=NOW(), processor_id=#{processorId} WHERE id=#{id}")
    int startProcessing(@Param("id") Long id, @Param("processorId") Long processorId);
    
    @Update("UPDATE processing_order SET status='COMPLETED', complete_time=NOW() WHERE id=#{id}")
    int completeProcessing(Long id);
    
    @Update("UPDATE processing_order SET quality_check_result=#{result}, " +
            "quality_check_remark=#{remark}, status=IF(#{result}='PASS', 'COMPLETED', 'QUALITY_CHECK') " +
            "WHERE id=#{id}")
    int qualityCheck(@Param("id") Long id, @Param("result") String result, @Param("remark") String remark);
    
    @Select("SELECT MAX(order_no) FROM processing_order WHERE order_no LIKE CONCAT('JG', DATE_FORMAT(NOW(), '%Y%m%d'), '%')")
    String getMaxOrderNoToday();
}