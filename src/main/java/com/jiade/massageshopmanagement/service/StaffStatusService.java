package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.StaffStatusRecord;
import com.jiade.massageshopmanagement.dto.StaffStatusRequest;
import com.jiade.massageshopmanagement.dto.StaffStatusResponse;
import com.jiade.massageshopmanagement.mapper.StaffStatusMapper;
import com.jiade.massageshopmanagement.model.Staff;
import com.jiade.massageshopmanagement.model.StaffAttendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaffStatusService {

    @Autowired
    private StaffStatusMapper staffStatusMapper;
    public static final String STATUS_PRESENT = "到岗";
    public static final String STATUS_ABSENT = "未到岗";

    public void addStaffStatus(Long staffId, StaffStatusRequest request) {
        try {
            if (request.getStatus() != null && !request.getStatus().equals(STATUS_PRESENT) && !request.getStatus().equals(STATUS_ABSENT)) {
                throw new IllegalArgumentException("员工状态字段仅允许'到岗'或'未到岗'");
            }

            // 检查员工是否存在且未被删除
            Staff staff = staffStatusMapper.selectStaffById(staffId);
            if (staff == null || Boolean.TRUE.equals(staff.getIsDeleted())) {
                throw new IllegalArgumentException("员工不存在或已被删除");
            }
            // 检查当天是否已有记录
            StaffAttendance existing = staffStatusMapper.selectByStaffIdAndDate(staffId, request.getDate());
            if (existing != null) {
                throw new IllegalArgumentException("当天状态已存在，请勿重复添加");
            }
            StaffAttendance status = new StaffAttendance();
            status.setStaffId(staffId);
            status.setDate(request.getDate());
            status.setStatus(request.getStatus());
            status.setRemark(request.getRemark());
            staffStatusMapper.insertStaffStatus(status);
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
          throw new RuntimeException("添加员工状态失败" + e.getMessage(), e);
        }
    }

    public void updateStaffStatus(Long attendanceId, StaffStatusRequest request) {
        try {
            if (request.getStatus() != null && !request.getStatus().equals(STATUS_PRESENT) && !request.getStatus().equals(STATUS_ABSENT)) {
                throw new IllegalArgumentException("员工状态字段仅允许'到岗'或'未到岗'");
            }

            StaffAttendance existing = staffStatusMapper.selectById(attendanceId);
            if (existing == null) {
                throw new IllegalArgumentException("每日状态记录不存在");
            }
            int other = staffStatusMapper.countDuplicateAttendance(attendanceId, request.getDate());
            if (other >= 1) {
                throw new IllegalArgumentException("该员工在该日期已有状态记录，不能重复！");
            }
            // 可加更多业务逻辑校验，如日期不能重复等
            StaffAttendance updated = new StaffAttendance();
            updated.setDate(request.getDate());
            updated.setStatus(request.getStatus());
            updated.setRemark(request.getRemark());
            staffStatusMapper.updateStaffAttendance(attendanceId, updated);
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("更新员工状态失败" + e.getMessage(), e);
        }
    }

    public List<String> getStaffStatusCategories() {
        return List.of(STATUS_PRESENT, STATUS_ABSENT);
    }

    public StaffStatusResponse getStaffStatusList(
            String keyword, String status, LocalDate startDate, LocalDate endDate,
            String sortBy, String order, int page, int size) {
        // 默认参数赋值
        if (sortBy == null || (!sortBy.equals("staff_name") && !sortBy.equals("date"))) {
            sortBy = "date";
        }
        if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
            order = "desc";
        }
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int offset = (page - 1) * size;

        // 查询总数
        int totalRecords = staffStatusMapper.countStaffStatusList(keyword, status, startDate, endDate);
        int totalPages = (int) Math.ceil(totalRecords * 1.0 / size);

        // 查询列表
        List<StaffStatusRecord> records = staffStatusMapper.selectStaffStatusList(
                keyword, status, startDate, endDate, sortBy, order, offset, size);

        return new StaffStatusResponse(records, totalRecords, totalPages, page);
    }

}
