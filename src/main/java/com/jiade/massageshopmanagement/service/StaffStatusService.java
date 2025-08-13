package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.StaffStatusRecord;
import com.jiade.massageshopmanagement.dto.StaffStatusRequest;
import com.jiade.massageshopmanagement.dto.StaffStatusResponse;
import com.jiade.massageshopmanagement.enums.OperationModule;
import com.jiade.massageshopmanagement.enums.OperationType;
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
    @Autowired
    private OperationLogService operationLogService;
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

            // 日志记录
            String logDetail = String.format(
                    "新增员工状态，员工ID：%d，姓名：%s，日期：%s，状态：%s，备注：%s",
                    staff.getId(),
                    staff.getName(),
                    request.getDate(),
                    request.getStatus(),
                    request.getRemark()
            );

            operationLogService.recordLog(
                    OperationType.CREATE,
                    OperationModule.STAFF_STATUS,
                    logDetail
            );
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

            // 日志记录
            // 如需员工姓名，可加：Staff staff = staffStatusMapper.selectStaffById(existing.getStaffId());
            // 先查出员工对象
            Staff staff = staffStatusMapper.selectStaffById(existing.getStaffId());
            // 如果查不到也要兼容
            String staffName = (staff != null) ? staff.getName() : "未知";

            String logDetail = String.format(
                    "更新员工状态，考勤ID：%d，员工ID：%d，姓名：%s，原日期：%s，原状态：%s，原备注：%s；新日期：%s，新状态：%s，新备注：%s",
                    attendanceId,
                    existing.getStaffId(),
                    staffName,
                    existing.getDate(),
                    existing.getStatus(),
                    existing.getRemark(),
                    request.getDate(),
                    request.getStatus(),
                    request.getRemark()
            );

            operationLogService.recordLog(
                    OperationType.UPDATE,
                    OperationModule.STAFF_STATUS,
                    logDetail
            );
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
