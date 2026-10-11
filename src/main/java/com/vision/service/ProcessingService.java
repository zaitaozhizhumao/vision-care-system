package com.vision.service;

import com.vision.dto.PageQuery;
import com.vision.dto.Result;
import com.vision.entity.Prescription;
import com.vision.entity.ProcessingOrder;
import com.vision.mapper.PrescriptionMapper;
import com.vision.mapper.ProcessingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProcessingService {

    private final ProcessingMapper processingMapper;
    private final PrescriptionMapper prescriptionMapper;

    public ProcessingService(ProcessingMapper processingMapper, PrescriptionMapper prescriptionMapper) {
        this.processingMapper = processingMapper;
        this.prescriptionMapper = prescriptionMapper;
    }

    /**
     * 分页查询加工单
     */
    public Result<?> pageQuery(PageQuery query) {
        Integer offset = query.getOffset();
        List<ProcessingOrder> list = processingMapper.findByPage(
                query.getKeyword(), query.getStatus(), offset, query.getLimit());
        Long count = processingMapper.countByCondition(query.getKeyword(), query.getStatus());
        return Result.success(list, count);
    }

    /**
     * 根据ID查询加工单
     */
    public ProcessingOrder findById(Long id) {
        return processingMapper.findById(id);
    }

    /**
     * 从处方创建加工单
     */
    @Transactional
    public Result<?> createFromPrescription(Long prescriptionId, Long processorId) {
        Prescription prescription = prescriptionMapper.findById(prescriptionId);
        if (prescription == null) {
            return Result.error("处方不存在");
        }

        ProcessingOrder order = new ProcessingOrder();
        order.setPrescriptionId(prescriptionId);
        order.setProcessorId(processorId);
        order.setFrameModel(prescription.getFrameModel());
        order.setFrameBrand(prescription.getFrameBrand());
        order.setLensType(prescription.getLensType());
        order.setLensIndex(prescription.getLensIndex());
        order.setStatus("PENDING");

        // 生成加工单号
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String maxNo = processingMapper.getMaxOrderNoToday();
        int seq = 1;
        if (maxNo != null && maxNo.length() >= 12) {
            try {
                seq = Integer.parseInt(maxNo.substring(10)) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        order.setOrderNo("JG" + dateStr + String.format("%04d", seq));

        int result = processingMapper.insert(order);

        if (result > 0) {
            // 更新处方状态为加工中
            prescriptionMapper.updateStatus(prescriptionId, "PROCESSING");
            return Result.success("加工单创建成功", order);
        } else {
            return Result.error("创建失败");
        }
    }

    /**
     * 开始加工
     */
    @Transactional
    public Result<?> startProcessing(Long id, Long processorId) {
        ProcessingOrder order = processingMapper.findById(id);
        if (order == null) {
            return Result.error("加工单不存在");
        }

        int result = processingMapper.startProcessing(id, processorId);
        if (result > 0) {
            // 更新处方状态
            prescriptionMapper.updateStatus(order.getPrescriptionId(), "PROCESSING");
        }
        return result > 0 ? Result.success("开始加工") : Result.error("操作失败");
    }

    /**
     * 完成加工
     */
    @Transactional
    public Result<?> completeProcessing(Long id) {
        ProcessingOrder order = processingMapper.findById(id);
        if (order == null) {
            return Result.error("加工单不存在");
        }

        int result = processingMapper.completeProcessing(id);
        if (result > 0) {
            // 更新处方状态为待质检
            processingMapper.updateStatus(id, "QUALITY_CHECK");
        }
        return result > 0 ? Result.success("加工完成，等待质检") : Result.error("操作失败");
    }

    /**
     * 质检
     */
    @Transactional
    public Result<?> qualityCheck(Long id, String result, String remark) {
        ProcessingOrder order = processingMapper.findById(id);
        if (order == null) {
            return Result.error("加工单不存在");
        }

        String checkResult = "PASS".equals(result) ? "PASS" : "FAIL";
        int rows = processingMapper.qualityCheck(id, checkResult, remark);

        if (rows > 0) {
            if ("PASS".equals(result)) {
                // 质检合格，加工单完成
                processingMapper.updateStatus(id, "COMPLETED");
                // 处方状态保持加工中，等待交付
            } else {
                // 质检不合格，退回加工
                processingMapper.updateStatus(id, "PROCESSING");
                prescriptionMapper.updateStatus(order.getPrescriptionId(), "PROCESSING");
            }
            return Result.success("质检完成");
        }
        return Result.error("质检失败");
    }

    /**
     * 更新状态
     */
    @Transactional
    public Result<?> updateStatus(Long id, String status) {
        ProcessingOrder order = processingMapper.findById(id);
        if (order == null) {
            return Result.error("加工单不存在");
        }

        int result = processingMapper.updateStatus(id, status);

        if (result > 0) {
            // 同步更新处方状态
            if ("DELIVERED".equals(status)) {
                // 交付完成，处方状态改为已完成
                prescriptionMapper.updateStatus(order.getPrescriptionId(), "COMPLETED");
            } else if ("PROCESSING".equals(status)) {
                prescriptionMapper.updateStatus(order.getPrescriptionId(), "PROCESSING");
            }
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }
}