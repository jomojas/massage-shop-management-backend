package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.MemberListResponse;
import com.jiade.massageshopmanagement.dto.ProjectCreateRequest;
import com.jiade.massageshopmanagement.dto.ProjectListResponse;
import com.jiade.massageshopmanagement.dto.ProjectUpdateRequest;
import com.jiade.massageshopmanagement.enums.OperationModule;
import com.jiade.massageshopmanagement.enums.OperationType;
import com.jiade.massageshopmanagement.mapper.ProjectMapper;
import com.jiade.massageshopmanagement.model.Project;
import com.jiade.massageshopmanagement.model.ProjectCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {


    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private OperationLogService operationLogService;

    public ProjectListResponse getProjects(String keyword, String category, String sortBy, String order, int page, int size) {

        int offset = (page - 1) * size;
        List<Project> projects = projectMapper.selectProjectsByFilters(
                keyword, category, sortBy, order, offset, size);
        int totalProjects = projectMapper.countProjectsByFilters(
                keyword, category);
        int totalPages = (int) Math.ceil((double) totalProjects / size);

        return new ProjectListResponse(projects, totalProjects, totalPages, page); // Placeholder return statement
    }

    public ProjectListResponse getDeletedProjects(String keyword, String category, String sortBy, String order, int page, int size) {

        int offset = (page - 1) * size;
        List<Project> projects = projectMapper.selectDeletedProjectsByFilters(
                keyword, category, sortBy, order, offset, size);
        int totalProjects = projectMapper.countDeletedProjectsByFilters(
                keyword, category);
        int totalPages = (int) Math.ceil((double) totalProjects / size);

        return new ProjectListResponse(projects, totalProjects, totalPages, page); // Placeholder return statement
    }

    public void addProject(ProjectCreateRequest request) {
        try {
            // 检查项目名称是否已存在
            List<Project> projects = projectMapper.selectProjectsByName(request.getName());
            if (projects != null && !projects.isEmpty()) {
                for (Project p : projects) {
                    if (p.getIsDeleted() == 0) {
                        throw new IllegalArgumentException("项目已存在: " + request.getName());
                    }
                    if (p.getIsDeleted() == 1) {
                        throw new IllegalArgumentException("该项目存在但被删除，请恢复: " + request.getName());
                    }
                }
            }
            Project project = new Project();
            project.setName(request.getName());
            project.setCategory(request.getCategory());
            project.setPriceGuest(request.getPriceGuest());
            project.setPriceMember(request.getPriceMember());
            project.setDescription(request.getDescription());
            projectMapper.insertProject(project);

            // 日志记录
            String logDetail = String.format(
                    "新增项目，ID：%d，名称：%s，分类：%s，散客价：%s，会员价：%s，描述：%s",
                    project.getId(),
                    project.getName(),
                    project.getCategory(),
                    project.getPriceGuest(),
                    project.getPriceMember(),
                    project.getDescription()
            );

            operationLogService.recordLog(
                    OperationType.CREATE,
                    OperationModule.PROJECT,
                    logDetail
            );
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("添加项目失败", e);
        }
    }

    public void updateProject(Long id, ProjectUpdateRequest request) {
        try {
            // 1. 检查当前项目是否存在
            Project current = projectMapper.selectProjectById(id);
            if (current == null) {
                throw new IllegalArgumentException("该项目不存在");
            }
            // 2. 检查项目是否已被删除
            if (current.getIsDeleted() == 1) {
                throw new IllegalArgumentException("该项目已被删除，请先恢复项目后再修改: " + id);
            }
            // 3. 检查是否有其他同名项目（排除自己）
            List<Project> sameNameProjects = projectMapper.selectProjectsByName(request.getName());
            for (Project p : sameNameProjects) {
                if (!p.getId().equals(id)) {
                    if (p.getIsDeleted() == 0) {
                        throw new IllegalArgumentException("项目名称已被使用: " + request.getName());
                    }
                    if (p.getIsDeleted() == 1) {
                        throw new IllegalArgumentException("已有被删除项目使用该名称，请先恢复该项目再修改: " + request.getName());
                    }
                }
            }
            Project project = new Project();
            project.setName(request.getName());
            project.setCategory(request.getCategory());
            project.setPriceGuest(request.getPriceGuest());
            project.setPriceMember(request.getPriceMember());
            project.setDescription(request.getDescription());
            projectMapper.updateProject(id, project);

            // 日志记录
            String logDetail = String.format(
                    "更新项目，ID：%d，原名称：%s，原分类：%s，原散客价：%s，原会员价：%s，原描述：%s；新名称：%s，新分类：%s，新散客价：%s，新会员价：%s，新描述：%s",
                    id,
                    current.getName(),
                    current.getCategory(),
                    current.getPriceGuest(),
                    current.getPriceMember(),
                    current.getDescription(),
                    request.getName(),
                    request.getCategory(),
                    request.getPriceGuest(),
                    request.getPriceMember(),
                    request.getDescription()
            );

            operationLogService.recordLog(
                    OperationType.UPDATE,
                    OperationModule.PROJECT,
                    logDetail
            );
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("更新项目失败", e);
        }
    }

    public void deleteProject(Long id) {
        try {
            Project current = projectMapper.selectProjectById(id);
            if (current == null) {
                throw new IllegalArgumentException("项目不存在");
            }
            if (current.getIsDeleted() == 1) {
                throw new IllegalArgumentException("项目已被删除");
            }
            projectMapper.logicDeleteProject(id);

            // 日志记录
            String logDetail = String.format(
                    "删除项目，ID：%d，名称：%s，分类：%s，散客价：%s，会员价：%s，描述：%s",
                    current.getId(),
                    current.getName(),
                    current.getCategory(),
                    current.getPriceGuest(),
                    current.getPriceMember(),
                    current.getDescription()
            );

            operationLogService.recordLog(
                    OperationType.DELETE,
                    OperationModule.PROJECT,
                    logDetail
            );
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("删除项目失败", e);
        }
    }

    public void restoreProject(Long id) {
        try {
            Project current = projectMapper.selectProjectById(id);
            if (current == null) {
                throw new IllegalArgumentException("项目不存在");
            }
            if (current.getIsDeleted() == 0) {
                throw new IllegalArgumentException("项目未被删除或已恢复");
            }
            projectMapper.logicRestoreProject(id);

            // 日志记录
            String logDetail = String.format(
                    "恢复项目，ID：%d，名称：%s，分类：%s，散客价：%s，会员价：%s，描述：%s",
                    current.getId(),
                    current.getName(),
                    current.getCategory(),
                    current.getPriceGuest(),
                    current.getPriceMember(),
                    current.getDescription()
            );

            operationLogService.recordLog(
                    OperationType.RESTORE,
                    OperationModule.PROJECT,
                    logDetail
            );
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("删除项目失败", e);
        }
    }

    public List<String> getAllCategories() {
        return projectMapper.getAllCategories().stream()
                .map(ProjectCategory::getCategory)
                .collect(Collectors.toList());
    }

    public void addProjectCategory(String category) {
        try {
            if (projectMapper.countByCategory(category) > 0) {
                throw new IllegalArgumentException("类别已存在");
            }
            projectMapper.insertCategory(category);

            // 日志记录
            String logDetail = String.format("新增项目类别，名称：%s", category);

            operationLogService.recordLog(
                    OperationType.CREATE,
                    OperationModule.PROJECT,
                    logDetail
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("添加项目类别失败", e);
        }
    }
}
