package com.smartx.auth.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.smartx.auth.domain.entity.Customer;
import com.smartx.auth.domain.entity.Supplier;
import com.smartx.auth.mapper.CustomerMapper;
import com.smartx.auth.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.smartx.auth.domain.entity.table.CustomerTableDef.CUSTOMER;
import static com.smartx.auth.domain.entity.table.SupplierTableDef.SUPPLIER;

@Service
public class PartnerService {

    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private SupplierMapper supplierMapper;

    // 获取所有有效客户
    public List<Customer> getAllCustomers() {
        return customerMapper.selectListByQuery(
                QueryWrapper.create().from(CUSTOMER).where(CUSTOMER.IS_DELETED.eq(0))
        );
    }

    // 获取所有有效供应商
    public List<Supplier> getAllSuppliers() {
        return supplierMapper.selectListByQuery(
                QueryWrapper.create().from(SUPPLIER).where(SUPPLIER.IS_DELETED.eq(0))
        );
    }
}
