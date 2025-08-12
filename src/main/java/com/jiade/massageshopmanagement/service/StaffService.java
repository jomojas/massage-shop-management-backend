package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.*;
import com.jiade.massageshopmanagement.model.Staff;
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

    public StaffListResponse queryStaffList(
            String keyword,
            BigDecimal commissionMin,
            BigDecimal commissionMax,
            String sortBy,
            String order,
            int page,
            int pageSize
    ) {
        // 参数预处理
        int offset = (page - 1) * pageSize;
        // 查询总数
        int totalEmployees = staffMapper.countStaffs(keyword, commissionMin, commissionMax);
        // 查询员工列表
        List<Staff> employees = staffMapper.selectStaffs(
                keyword, commissionMin, commissionMax, sortBy, order, pageSize, offset
        );
        int totalPages = (int) Math.ceil((double) totalEmployees / pageSize);

        StaffListResponse response = new StaffListResponse();
        response.setTotalEmployees(totalEmployees);
        response.setTotalPages(totalPages);
        response.setCurrentPage(page);
        response.setEmployees(employees);
        return response;
    }

    public StaffListResponse queryDeletedStaffList(
            String keyword,
            BigDecimal commissionMin,
            BigDecimal commissionMax,
            String sortBy,
            String order,
            int page,
            int pageSize
    ) {
        // 参数预处理
        int offset = (page - 1) * pageSize;
        // 查询总数
        int totalEmployees = staffMapper.countDeletedStaffs(keyword, commissionMin, commissionMax);
        // 查询员工列表
        List<Staff> employees = staffMapper.selectDeletedStaffs(
                keyword, commissionMin, commissionMax, sortBy, order, pageSize, offset
        );
        int totalPages = (int) Math.ceil((double) totalEmployees / pageSize);

        StaffListResponse response = new StaffListResponse();
        response.setTotalEmployees(totalEmployees);
        response.setTotalPages(totalPages);
        response.setCurrentPage(page);
        response.setEmployees(employees);
        return response;
    }

    public void addStaff(StaffAddRequest request) {
        try {
            // 检查手机号是否已存在
            List<Staff> existingStaff = staffMapper.selectStaffByPhone(request.getPhone());
            if (existingStaff != null && !existingStaff.isEmpty()) {
                throw new IllegalArgumentException("手机号已存在,请不要重复添加员工");
            }
            // 检查是否有同名同号的已删除员工
            List<Staff> deletedStaff = staffMapper.selectDeletedStaffByNameAndPhone(request.getName(), request.getPhone());
            if (deletedStaff != null && !deletedStaff.isEmpty()) {
                throw new IllegalArgumentException("该员工曾被删除，请先恢复该员工");
            }
            // 创建新员工对象
            Staff staff = new Staff();
            staff.setName(request.getName());
            staff.setPhone(request.getPhone());
            staff.setCommission(request.getCommission());
            // 插入新员工
            staffMapper.insertStaff(staff);
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("添加员工失败" + e.getMessage(), e);
        }
    }

    public void updateStaff(Long id, StaffUpdateRequest request) {
        try {
            // 检查员工是否被删除
            Staff staffOrigin = staffMapper.selectStaffById(id);
            if (staffOrigin == null) {
                throw new IllegalArgumentException("员工不存在");
            }
            if (Boolean.TRUE.equals(staffOrigin.getIsDeleted())) {
                throw new IllegalArgumentException("该员工已被删除，请先恢复员工再进行修改");
            }

            // 检查手机号是否已存在
            List<Staff> existingStaff = staffMapper.selectStaffByPhone(request.getPhone());
            if (existingStaff != null && !existingStaff.isEmpty()) {
                for (Staff s : existingStaff) {
                    if (!s.getId().equals(id)) { // 确保不是更新同一员工
                        throw new IllegalArgumentException("手机号已存在,请不要重复添加员工");
                    }
                }
            }
            // 更新员工信息
            Staff staff = new Staff();
            staff.setName(request.getName());
            staff.setPhone(request.getPhone());
            staff.setCommission(request.getCommission());
            staffMapper.updateStaff(id, staff);
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("更新员工失败" + e.getMessage(), e);
        }
    }

    public void deleteStaff(Long id) {
        try {
            Staff staff = staffMapper.selectStaffById(id);
            if (staff == null) {
                throw new IllegalArgumentException("员工不存在");
            }
            if (Boolean.TRUE.equals(staff.getIsDeleted())) {
                throw new IllegalArgumentException("员工已被删除");
            }
            staffMapper.deleteStaff(id);
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("删除员工失败", e);
        }
    }

    public void restoreStaff(Long id) {
        try {
            Staff staff = staffMapper.selectStaffById(id);
            if (staff == null) {
                throw new IllegalArgumentException("员工不存在");
            }
            if (Boolean.FALSE.equals(staff.getIsDeleted())) {
                throw new IllegalArgumentException("员工未被删除，无需恢复");
            }
            staffMapper.restoreStaff(id);
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("恢复员工失败", e);
        }
    }
}
