package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.model.AdminAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {

    AdminAccount selectById(@Param("id") String id);

    // 根据主键更新全部字段
    int updateById(AdminAccount account);
}
