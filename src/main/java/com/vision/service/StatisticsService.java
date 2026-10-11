package com.vision.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 统计服务类
 */
@Service
public class StatisticsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取仪表盘统计数据
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 患者总数
            Long patientCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM patient WHERE deleted = 0", Long.class);
            result.put("patientCount", patientCount != null ? patientCount : 0L);
        } catch (Exception e) {
            result.put("patientCount", 0L);
        }

        try {
            // 今日验光数
            Long todayOptometry = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM optometry_record WHERE DATE(check_date) = CURDATE()", Long.class);
            result.put("todayOptometry", todayOptometry != null ? todayOptometry : 0L);
        } catch (Exception e) {
            result.put("todayOptometry", 0L);
        }

        try {
            // 待处理加工单
            Long pendingProcess = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM processing_order WHERE status IN ('PENDING', 'PROCESSING', 'QUALITY_CHECK')",
                    Long.class);
            result.put("pendingProcess", pendingProcess != null ? pendingProcess : 0L);
        } catch (Exception e) {
            result.put("pendingProcess", 0L);
        }

        try {
            // 今日待随访
            Long todayFollowUp = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM follow_up_plan WHERE status = 'PENDING' AND plan_date <= CURDATE()",
                    Long.class);
            result.put("todayFollowUp", todayFollowUp != null ? todayFollowUp : 0L);
        } catch (Exception e) {
            result.put("todayFollowUp", 0L);
        }

        try {
            // 本月配镜数量
            Long monthPrescription = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM prescription WHERE status = 'COMPLETED' AND MONTH(create_time) = MONTH(CURDATE())",
                    Long.class);
            result.put("monthPrescription", monthPrescription != null ? monthPrescription : 0L);
        } catch (Exception e) {
            result.put("monthPrescription", 0L);
        }

        try {
            // 本月营收
            Double monthRevenue = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(paid_amount), 0) FROM prescription WHERE MONTH(create_time) = MONTH(CURDATE())",
                    Double.class);
            result.put("monthRevenue", monthRevenue != null ? monthRevenue : 0.0);
        } catch (Exception e) {
            result.put("monthRevenue", 0.0);
        }

        try {
            // 近一周验光趋势
            List<Map<String, Object>> weekTrend = jdbcTemplate.queryForList(
                    "SELECT DATE(check_date) as date, COUNT(*) as count " +
                            "FROM optometry_record " +
                            "WHERE check_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                            "GROUP BY DATE(check_date) ORDER BY date");

            List<String> dates = new ArrayList<>();
            List<Long> counts = new ArrayList<>();
            for (Map<String, Object> row : weekTrend) {
                dates.add(row.get("date").toString());
                counts.add((Long) row.get("count"));
            }
            result.put("weekDates", dates);
            result.put("weekCounts", counts);
        } catch (Exception e) {
            result.put("weekDates", new ArrayList<>());
            result.put("weekCounts", new ArrayList<>());
        }

        return result;
    }

    /**
     * 获取近视趋势
     */
    public Map<String, Object> getMyopiaTrend(String period) {
        Map<String, Object> result = new HashMap<>();

        try {
            String sql = "SELECT DATE_FORMAT(check_date, '%Y-%m') as month, " +
                    "AVG(ABS(sphere_od) + ABS(sphere_os))/2 as avg_sphere, " +
                    "COUNT(*) as count " +
                    "FROM optometry_record " +
                    "WHERE sphere_od < 0 AND sphere_os < 0 AND status = 'CONFIRMED' " +
                    "GROUP BY DATE_FORMAT(check_date, '%Y-%m') " +
                    "ORDER BY month DESC LIMIT 12";

            List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);

            List<String> months = new ArrayList<>();
            List<Double> values = new ArrayList<>();
            List<Long> counts = new ArrayList<>();

            for (int i = data.size() - 1; i >= 0; i--) {
                Map<String, Object> row = data.get(i);
                months.add(row.get("month").toString());
                values.add(((Number) row.get("avg_sphere")).doubleValue());
                counts.add((Long) row.get("count"));
            }

            result.put("months", months);
            result.put("values", values);
            result.put("counts", counts);
        } catch (Exception e) {
            result.put("months", new ArrayList<>());
            result.put("values", new ArrayList<>());
            result.put("counts", new ArrayList<>());
        }

        return result;
    }

    /**
     * 获取年龄分布
     */
    public Map<String, Object> getAgeDistribution() {
        Map<String, Object> result = new HashMap<>();

        try {
            String sql = "SELECT " +
                    "CASE " +
                    "WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) < 12 THEN '12岁以下' " +
                    "WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 12 AND 18 THEN '12-18岁' " +
                    "WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 19 AND 30 THEN '19-30岁' " +
                    "WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 31 AND 45 THEN '31-45岁' " +
                    "WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 46 AND 60 THEN '46-60岁' " +
                    "ELSE '60岁以上' END as age_group, " +
                    "COUNT(*) as count " +
                    "FROM patient " +
                    "WHERE birth_date IS NOT NULL AND deleted = 0 " +
                    "GROUP BY age_group " +
                    "ORDER BY FIELD(age_group, '12岁以下', '12-18岁', '19-30岁', '31-45岁', '46-60岁', '60岁以上')";

            List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);

            List<String> groups = new ArrayList<>();
            List<Long> counts = new ArrayList<>();
            for (Map<String, Object> row : data) {
                groups.add(row.get("age_group").toString());
                counts.add((Long) row.get("count"));
            }

            result.put("groups", groups);
            result.put("counts", counts);
        } catch (Exception e) {
            result.put("groups", new ArrayList<>());
            result.put("counts", new ArrayList<>());
        }

        return result;
    }

    /**
     * 获取近视程度分布
     */
    public Map<String, Object> getMyopiaDegreeDistribution() {
        Map<String, Object> result = new HashMap<>();

        try {
            String sql = "SELECT " +
                    "CASE " +
                    "WHEN ABS(sphere_od) <= 3.00 AND ABS(sphere_os) <= 3.00 THEN '低度近视(≤-3.00D)' " +
                    "WHEN ABS(sphere_od) <= 6.00 AND ABS(sphere_os) <= 6.00 THEN '中度近视(-3.00D~-6.00D)' " +
                    "ELSE '高度近视(>-6.00D)' END as degree_group, " +
                    "COUNT(*) as count " +
                    "FROM optometry_record " +
                    "WHERE sphere_od < 0 AND sphere_os < 0 AND status = 'CONFIRMED' " +
                    "AND id IN (SELECT MAX(id) FROM optometry_record GROUP BY patient_id) " +
                    "GROUP BY degree_group";

            List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);

            List<String> groups = new ArrayList<>();
            List<Long> counts = new ArrayList<>();
            for (Map<String, Object> row : data) {
                groups.add(row.get("degree_group").toString());
                counts.add((Long) row.get("count"));
            }

            result.put("groups", groups);
            result.put("counts", counts);
        } catch (Exception e) {
            result.put("groups", new ArrayList<>());
            result.put("counts", new ArrayList<>());
        }

        return result;
    }
}