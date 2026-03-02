package com.smartx.sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartx.sales.domain.entity.SalesOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SalesOrderMapper extends BaseMapper<SalesOrder> {
}
