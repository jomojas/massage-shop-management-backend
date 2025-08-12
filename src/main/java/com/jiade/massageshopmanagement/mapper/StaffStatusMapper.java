package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.dto.StaffStatusRecord;
import com.jiade.massageshopmanagement.model.Staff;
import com.jiade.massageshopmanagement.model.StaffAttendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StaffStatusMapper {

    /**
     * 根据员工ID和日期查询员工状态
     * @param id 员工ID
     * @return 员工状态记录
     */
    Staff selectStaffById(@Param("id") Long id);

    /**
     * 插入员工状态记录
     * @param staffId 员工状态记录
     * @param date 员工状态记录日期
     * @return 符合条件的员工状态记录
     */
    StaffAttendance selectByStaffIdAndDate(@Param("staffId") Long staffId, @Param("date") LocalDate date);

    /**
     * 插入员工状态记录
     * @param staffAttendance 员工状态记录
     */
    void insertStaffStatus(@Param("staffAttendance") StaffAttendance staffAttendance);

    /**
     * 更新员工状态记录
     * @param attendanceId 员工状态记录ID
     */
    StaffAttendance selectById(@Param("attendanceId") Long attendanceId);

    /**
     * 更新员工状态记录
     * @param attendanceId 员工状态记录ID
     * @param staffAttendance 更新后的员工状态记录
     */
    void updateStaffAttendance(@Param("attendanceId") Long attendanceId, @Param("staffAttendance") StaffAttendance staffAttendance);

    /**
     * 统计重复的员工状态记录(同一个员工，同一天的记录)
     * @return 重复记录的数量
     */
    int countDuplicateAttendance(@Param("attendanceId") Long attendanceId, @Param("date") LocalDate date);

    /**
     * 查询员工状态列表
     * @param keyword 关键词（员工姓名或手机号）
     * @param status 员工状态（如 "到岗" 或 "未到岗"）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 符合条件的员工状态记录数量
     */
    int countStaffStatusList(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 分页查询员工状态列表
     * @param keyword 关键词（员工姓名或手机号）
     * @param status 员工状态（如 "到岗" 或 "未到岗"）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param sortBy 排序字段
     * @param order 排序方式（asc 或 desc）
     * @param offset 偏移量
     * @param size 每页大小
     * @return 符合条件的员工状态记录列表
     */
    List<StaffStatusRecord> selectStaffStatusList(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("size") int size
    );
}
