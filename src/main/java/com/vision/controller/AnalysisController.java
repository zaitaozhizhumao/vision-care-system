package com.vision.controller;

import com.vision.dto.Result;
import com.vision.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据分析控制器
 */
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取仪表盘数据
     */
    @GetMapping("/dashboard")
    public Result<?> getDashboardData() {
        Map<String, Object> data = statisticsService.getDashboardStatistics();
        return Result.success(data);
    }
    
    /**
     * 获取近视趋势
     */
    @GetMapping("/myopia/trend")
    public Result<?> getMyopiaTrend(@RequestParam(required = false, defaultValue = "12") String period) {
        Map<String, Object> data = statisticsService.getMyopiaTrend(period);
        return Result.success(data);
    }
    
    /**
     * 获取年龄分布
     */
    @GetMapping("/age/distribution")
    public Result<?> getAgeDistribution() {
        Map<String, Object> data = statisticsService.getAgeDistribution();
        return Result.success(data);
    }
    
    /**
     * 获取近视程度分布
     */
    @GetMapping("/myopia/degree")
    public Result<?> getMyopiaDegreeDistribution() {
        Map<String, Object> data = statisticsService.getMyopiaDegreeDistribution();
        return Result.success(data);
    }
}