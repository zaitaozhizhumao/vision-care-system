package com.vision.dto;

import lombok.Data;

/**
 * 分页查询参数类
 */
@Data
public class PageQuery {

    /**
     * 当前页码，默认第1页
     */
    private Integer page = 1;

    /**
     * 每页条数，默认10条
     */
    private Integer limit = 10;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 状态筛选
     */
    private String status;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 计算偏移量（用于SQL的LIMIT offset, limit）
     * 
     * @return 偏移量
     */
    public Integer getOffset() {
        return (page - 1) * limit;
    }
}