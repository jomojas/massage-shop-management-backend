package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.StaffServiceRecordDTO;
import com.jiade.massageshopmanagement.dto.StaffServiceRecordResponse;
import com.jiade.massageshopmanagement.mapper.StaffMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaffService {

    @Autowired
    private StaffMapper staffMapper;

    public StaffServiceRecordResponse queryServiceRecords(
            String keyword,
            LocalDateTime dateStart,
            LocalDateTime dateEnd,
            BigDecimal earningsMin,
            BigDecimal earningsMax,
            String sortBy,
            String order,
            int page,
            int pageSize
    ) {
        try {
            // 1. 统计总记录数
            int totalRecords = staffMapper.countServiceRecords(
                    keyword, dateStart, dateEnd,
                    earningsMin, earningsMax
            );

            // 2. 计算分页
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            int offset = (page - 1) * pageSize;

            // 3. 查询当前页数据
            List<StaffServiceRecordDTO> records = staffMapper.selectServiceRecords(
                    keyword, dateStart, dateEnd,
                    earningsMin, earningsMax,
                    sortBy, order, offset, pageSize
            );

            // 4. 返回封装对象
            StaffServiceRecordResponse response = new StaffServiceRecordResponse();
            response.setTotalRecords(totalRecords);
            response.setTotalPages(totalPages);
            response.setCurrentPage(page);
            response.setRecords(records);
            return response;
        } catch (Exception e) {
            // 处理异常，记录日志等
            throw new RuntimeException("查询服务记录失败", e);
        }
    }
}
