package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.*;
import com.jiade.massageshopmanagement.enums.ProjectCategory;
import com.jiade.massageshopmanagement.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * 获取项目列表
     * @param keyword 关键词搜索
     * @param category 项目类别
     * @param sortBy 排序字段
     * @param order 排序方式
     * @param page 页码
     * @param size 每页大小
     * @return 项目列表响应
     */
    @GetMapping("")
    public ApiResponse<ProjectListResponse> getProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        // 排序字段白名单
        List<String> allowedSortBy = Arrays.asList("name", "price_guest", "price_member");
        if (!allowedSortBy.contains(sortBy)) {
            sortBy = "name";
        }
        // 排序方式白名单
        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            order = "ASC";
        }
        ProjectListResponse response = projectService.getProjects(keyword, category, sortBy, order, page, size);
        return ApiResponse.success(response);
    }

    /**
     * 获取已删除的项目列表
     * @param keyword 关键词搜索
     * @param category 项目类别
     * @param sortBy 排序字段
     * @param order 排序方式
     * @param page 页码
     * @param size 每页大小
     * @return 已删除项目列表响应
     */
    @GetMapping("/deleted")
    public ApiResponse<ProjectListResponse> getDeletedProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        // 排序字段白名单
        List<String> allowedSortBy = Arrays.asList("name", "price_guest", "price_member");
        if (!allowedSortBy.contains(sortBy)) {
            sortBy = "name";
        }
        // 排序方式白名单
        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            order = "ASC";
        }
        ProjectListResponse response = projectService.getDeletedProjects(keyword, category, sortBy, order, page, size);
        return ApiResponse.success(response);
    }

    /**
     * 添加新项目
     * @param request 项目创建请求
     * @return 操作结果
     */
    @PostMapping("")
    public ResponseEntity<?> addProject(@RequestBody ProjectCreateRequest request) {
        try {
            // 校验 name
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("项目名称不能为空");
            }
            // 校验 category（枚举）
            if (!ProjectCategory.isValid(request.getCategory())) {
                throw new IllegalArgumentException("项目类别不合法");
            }
            // 校验价格
            if (request.getPriceGuest() == null || request.getPriceMember() == null
                    || request.getPriceGuest().compareTo(BigDecimal.ZERO) < 0
                    || request.getPriceMember().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("价格必须为非负数");
            }
            // 业务调用（ProjectService.insertProject...）
            projectService.addProject(request);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }

    /**
     * 更新项目
     * @param id 项目ID
     * @param request 项目更新请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateRequest request) {
        try {
            // 校验 name
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("项目名称不能为空");
            }
            // 校验 category（枚举）
            if (!ProjectCategory.isValid(request.getCategory())) {
                throw new IllegalArgumentException("项目类别不合法");
            }
            // 校验价格
            if (request.getPriceGuest() == null || request.getPriceMember() == null
                    || request.getPriceGuest().compareTo(BigDecimal.ZERO) < 0
                    || request.getPriceMember().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("价格必须为非负数");
            }
            projectService.updateProject(id, request);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }

    /**
     * 恢复已删除的项目
     * @param id 项目ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }

    /**
     * 恢复已删除的项目
     * @param id 项目ID
     * @return 操作结果
     */
    @PatchMapping("/{id}/restore")
    public ResponseEntity<?> restoreProject(@PathVariable Long id) {
        try {
            projectService.restoreProject(id);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }
}
