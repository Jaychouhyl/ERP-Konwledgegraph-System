package com.smartx.auth.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.smartx.auth.domain.entity.Customer;
import com.smartx.auth.domain.entity.Supplier;
import com.smartx.auth.mapper.CustomerMapper;
import com.smartx.auth.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 引入 MyBatis-Flex 的字段常量
import static com.smartx.auth.domain.entity.table.CustomerTableDef.CUSTOMER;
import static com.smartx.auth.domain.entity.table.SupplierTableDef.SUPPLIER;

/**
 * 🤝 业务伙伴服务：供应商 & 客户管理
 */
@Service
public class PartnerService {

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private CustomerMapper customerMapper;

    // ================= 供应商 (Supplier) 业务 =================

    public boolean addSupplier(Supplier supplier) {
        return supplierMapper.insert(supplier) > 0;
    }

    public boolean updateSupplier(Supplier supplier) {
        return supplierMapper.update(supplier) > 0;
    }

    public boolean deleteSupplier(Long id) {
        return supplierMapper.deleteById(id) > 0;
    }

    public Supplier getSupplierById(Long id) {
        return supplierMapper.selectOneById(id);
    }

    public List<Supplier> listAllSuppliers() {
        return supplierMapper.selectListByQuery(QueryWrapper.create());
    }

    public Page<Supplier> pageSupplier(int pageNum, int pageSize, String keyword) {
        QueryWrapper query = QueryWrapper.create();
        if (keyword != null && !keyword.isEmpty()) {
            // 假设你的实体类有 supplierName 和 contactPerson 字段，如果没有请自行替换
            query.where(SUPPLIER.SUPPLIER_NAME.like(keyword))
                    .or(SUPPLIER.CONTACT_PERSON.like(keyword));
        }
        query.orderBy(SUPPLIER.ID.desc());
        return supplierMapper.paginate(pageNum, pageSize, query);
    }


    // ================= 客户 (Customer) 业务 =================

    public boolean addCustomer(Customer customer) {
        return customerMapper.insert(customer) > 0;
    }

    public boolean updateCustomer(Customer customer) {
        return customerMapper.update(customer) > 0;
    }

    public boolean deleteCustomer(Long id) {
        return customerMapper.deleteById(id) > 0;
    }

    public Customer getCustomerById(Long id) {
        return customerMapper.selectOneById(id);
    }

    public List<Customer> listAllCustomers() {
        return customerMapper.selectListByQuery(QueryWrapper.create());
    }

    public Page<Customer> pageCustomer(int pageNum, int pageSize, String keyword) {
        QueryWrapper query = QueryWrapper.create();
        if (keyword != null && !keyword.isEmpty()) {
            query.where(CUSTOMER.CUSTOMER_NAME.like(keyword))
                    .or(CUSTOMER.CONTACT_PHONE.like(keyword));
        }
        query.orderBy(CUSTOMER.ID.desc());
        return customerMapper.paginate(pageNum, pageSize, query);
    }
}