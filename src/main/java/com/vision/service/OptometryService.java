package com.vision.service;

import com.vision.dto.PageQuery;
import com.vision.dto.Result;
import com.vision.dto.VisionAnalysisDTO;
import com.vision.entity.OptometryRecord;
import com.vision.mapper.OptometryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 验光服务类
 * 负责验光记录的业务逻辑处理
 */
@Service
public class OptometryService {

    @Autowired
    private OptometryMapper optometryMapper;

    /**
     * 分页查询验光记录
     */
    public Result<?> pageQuery(Long patientId, PageQuery query) {
        try {
            Integer offset = query.getOffset();
            List<OptometryRecord> list = optometryMapper.findByPage(
                    patientId, query.getKeyword(), offset, query.getLimit());
            Long count = optometryMapper.countByCondition(patientId, query.getKeyword());
            return Result.success(list, count);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据患者ID查询验光记录
     */
    public List<OptometryRecord> findByPatientId(Long patientId) {
        return optometryMapper.findByPatientId(patientId);
    }

    /**
     * 根据ID查询验光记录
     */
    public OptometryRecord findById(Long id) {
        return optometryMapper.findById(id);
    }

    /**
     * 保存验光记录（新增或更新）
     */
    @Transactional
    public Result<?> save(OptometryRecord record) {
        try {
            // 设置默认值
            if (record.getCheckDate() == null) {
                record.setCheckDate(LocalDateTime.now());
            }

            // 验证必填字段
            if (record.getPatientId() == null) {
                return Result.error("请选择患者");
            }

            System.out.println("========== 保存验光记录 ==========");
            System.out.println("记录ID: " + record.getId());
            System.out.println("患者ID: " + record.getPatientId());
            System.out.println("验光师ID: " + record.getOptometristId());
            System.out.println("检查时间: " + record.getCheckDate());
            System.out.println("状态: " + record.getStatus());

            // 判断是新增还是更新
            if (record.getId() == null) {
                // 新增：设置初始状态为草稿
                record.setStatus("DRAFT");
                System.out.println("执行新增操作");
                int result = optometryMapper.insert(record);
                System.out.println("新增结果: " + result + ", 生成ID: " + record.getId());
                if (result > 0) {
                    return Result.success("保存成功", record);
                } else {
                    return Result.error("保存失败");
                }
            } else {
                // 更新：检查当前状态是否为草稿
                OptometryRecord existing = optometryMapper.findById(record.getId());
                if (existing == null) {
                    return Result.error("验光记录不存在");
                }
                if (!"DRAFT".equals(existing.getStatus())) {
                    return Result.error("只有草稿状态的记录可以编辑");
                }

                // 保持原有状态
                record.setStatus("DRAFT");
                System.out.println("执行更新操作, ID: " + record.getId());
                int result = optometryMapper.update(record);
                System.out.println("更新结果: " + result);
                if (result > 0) {
                    return Result.success("更新成功", record);
                } else {
                    return Result.error("更新失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("保存失败异常: " + e.getMessage());
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 确认验光记录
     * 将草稿状态改为已确认，确认后不可再修改
     */
    @Transactional
    public Result<?> confirm(Long id) {
        try {
            OptometryRecord record = optometryMapper.findById(id);
            if (record == null) {
                return Result.error("验光记录不存在");
            }
            if (!"DRAFT".equals(record.getStatus())) {
                return Result.error("只有草稿状态的记录可以确认");
            }

            int result = optometryMapper.updateStatus(id, "CONFIRMED");
            if (result > 0) {
                return Result.success("确认成功");
            } else {
                return Result.error("确认失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("确认失败: " + e.getMessage());
        }
    }

    /**
     * 作废验光记录
     * 将验光记录标记为作废，作废后不再参与后续业务流程
     */
    @Transactional
    public Result<?> cancel(Long id) {
        try {
            OptometryRecord record = optometryMapper.findById(id);
            if (record == null) {
                return Result.error("验光记录不存在");
            }
            if ("CANCELLED".equals(record.getStatus())) {
                return Result.error("该记录已经作废");
            }
            if ("CONFIRMED".equals(record.getStatus())) {
                return Result.error("已确认的记录不能作废，请先联系管理员");
            }

            int result = optometryMapper.updateStatus(id, "CANCELLED");
            if (result > 0) {
                return Result.success("作废成功");
            } else {
                return Result.error("作废失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("作废失败: " + e.getMessage());
        }
    }

    /**
     * 获取视力分析数据
     */
    public Map<String, Object> getVisionAnalysis(Long patientId) {
        List<VisionAnalysisDTO> trendData = optometryMapper.getVisionTrend(patientId);

        Map<String, Object> result = new HashMap<>();

        List<String> dates = new ArrayList<>();
        List<Double> sphereOd = new ArrayList<>();
        List<Double> sphereOs = new ArrayList<>();
        List<Double> visionOd = new ArrayList<>();
        List<Double> visionOs = new ArrayList<>();

        for (VisionAnalysisDTO dto : trendData) {
            dates.add(dto.getCheckDate().toLocalDate().toString());
            sphereOd.add(dto.getSphereOd() != null ? dto.getSphereOd().doubleValue() : null);
            sphereOs.add(dto.getSphereOs() != null ? dto.getSphereOs().doubleValue() : null);
            visionOd.add(dto.getVisionOdCorrected() != null ? dto.getVisionOdCorrected().doubleValue() : null);
            visionOs.add(dto.getVisionOsCorrected() != null ? dto.getVisionOsCorrected().doubleValue() : null);
        }

        result.put("dates", dates);
        result.put("sphereOd", sphereOd);
        result.put("sphereOs", sphereOs);
        result.put("visionOd", visionOd);
        result.put("visionOs", visionOs);

        return result;
    }

    /**
     * 获取今日验光数量
     */
    public Long countToday() {
        return optometryMapper.countToday();
    }
}