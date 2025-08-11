package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.model.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {

    /**
     * 根据过滤条件查询项目列表
     *
     * @param keyword 关键词
     * @param category 分类
     * @param sortBy 排序字段
     * @param order 排序方式（asc/desc）
     * @param offset 偏移量
     * @param size 每页大小
     * @return 项目列表
     */
    List<Project> selectProjectsByFilters(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 统计符合过滤条件的项目数量
     *
     * @param keyword 关键词
     * @param category 分类
     * @return 符合条件的项目数量
     */
    int countProjectsByFilters(@Param("keyword") String keyword, @Param("category") String category);

    /**
     * 根据过滤条件查询已删除的项目列表
     *
     * @param keyword 关键词
     * @param category 分类
     * @param sortBy 排序字段
     * @param order 排序方式（asc/desc）
     * @param offset 偏移量
     * @param size 每页大小
     * @return 已删除的项目列表
     */
    List<Project> selectDeletedProjectsByFilters(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 统计符合过滤条件的已删除项目数量
     *
     * @param keyword 关键词
     * @param category 分类
     * @return 符合条件的已删除项目数量
     */
    int countDeletedProjectsByFilters(@Param("keyword") String keyword, @Param("category") String category);

    /**
     * 插入新项目
     *
     * @param project 项目信息
     */
    void insertProject(Project project);

    /**
     * 根据项目名称查询项目列表
     *
     * @param name 项目名称
     * @return 项目列表
     */
    List<Project> selectProjectsByName(@Param("name") String name);

    /**
     * 根据项目ID查询项目
     *
     * @param id 项目ID
     * @return 项目信息
     */
    Project selectProjectById(@Param("id") Long id);

    /**
     * 更新项目
     *
     * @param id 项目ID
     * @param project 项目信息
     */
    void updateProject(@Param("id") Long id, @Param("project") Project project);

    /**
     * 逻辑删除项目
     *
     * @param id 项目ID
     */
    void logicDeleteProject(@Param("id") Long id);

    /**
     * 恢复逻辑删除的项目
     *
     * @param id 项目ID
     */
    void logicRestoreProject(@Param("id") Long id);
}
