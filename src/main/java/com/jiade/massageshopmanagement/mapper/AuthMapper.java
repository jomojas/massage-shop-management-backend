package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.model.AdminAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {

    /**
     * 根据用户名查询管理员账号
     *
     * @param username 用户名
     * @return 对应的AdminAccount对象
     */
    AdminAccount selectByUsername(@Param("username") String username);
}
