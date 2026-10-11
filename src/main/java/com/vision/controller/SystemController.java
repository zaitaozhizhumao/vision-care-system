package com.vision.controller;

import com.vision.dto.Result;
import com.vision.mapper.SysLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private SysLogMapper sysLogMapper;

    /**
     * 获取操作日志列表
     */
    @GetMapping("/log/page")
    public Result<?> getLogs(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit) {
        Integer offset = (page - 1) * limit;
        List<Map<String, Object>> list = sysLogMapper.findByPage(offset, limit);
        Long count = sysLogMapper.countAll();
        return Result.success(list, count);
    }
}