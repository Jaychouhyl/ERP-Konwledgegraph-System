package com.smartx.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartx.auth.domain.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {}
