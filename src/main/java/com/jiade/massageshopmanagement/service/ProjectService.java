package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.MemberListResponse;
import com.jiade.massageshopmanagement.dto.ProjectCreateRequest;
import com.jiade.massageshopmanagement.dto.ProjectListResponse;
import com.jiade.massageshopmanagement.dto.ProjectUpdateRequest;
import com.jiade.massageshopmanagement.mapper.ProjectMapper;
import com.jiade.massageshopmanagement.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

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
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("删除项目失败", e);
        }
    }
}
