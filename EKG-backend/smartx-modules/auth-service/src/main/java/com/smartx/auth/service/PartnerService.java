package com.smartx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartx.auth.domain.entity.Customer;
import com.smartx.auth.domain.entity.Supplier;
import com.smartx.auth.mapper.CustomerMapper;
import com.smartx.auth.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerService {

    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private SupplierMapper supplierMapper;

    // 获取所有有效客户
    public List<Customer> getAllCustomers() {
        return customerMapper.selectList(
                new LambdaQueryWrapper<Customer>().eq(Customer::getIsDeleted, 0)
        );
    }

    // 获取所有有效供应商
    public List<Supplier> getAllSuppliers() {
        return supplierMapper.selectList(
                new LambdaQueryWrapper<Supplier>().eq(Supplier::getIsDeleted, 0)
        );
    }
}
