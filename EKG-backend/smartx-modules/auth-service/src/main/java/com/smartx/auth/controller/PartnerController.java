package com.smartx.auth.controller;

import com.smartx.auth.domain.entity.Customer;
import com.smartx.auth.domain.entity.Supplier;
import com.smartx.auth.mapper.CustomerMapper;
import com.smartx.auth.mapper.SupplierMapper;
import com.smartx.auth.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smartx.common.core.domain.Result;

import java.util.List;

@RestController
@RequestMapping("/auth/partner")
public class PartnerController {

    @Autowired 
    private PartnerService partnerService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private SupplierMapper supplierMapper;

    @GetMapping("/customers")
    public Result<List<Customer>> listCustomers() {
        List<Customer> list = partnerService.getAllCustomers();
        return Result.success(list);
    }

    @GetMapping("/suppliers")
    public Result<List<Supplier>> listSuppliers() {
        List<Supplier> list = partnerService.getAllSuppliers();
        return Result.success(list);
    }

    // ========== 新增：客户的 CRUD ==========

    @PostMapping("/customer/add")
    public Result<Void> addCustomer(@RequestBody Customer customer) {
        customerMapper.insert(customer);
        return Result.success("新增客户成功！", null);
    }

    @PutMapping("/customer/update")
    public Result<Void> updateCustomer(@RequestBody Customer customer) {
        customerMapper.update(customer);
        return Result.success("修改客户资料成功！", null);
    }

    @DeleteMapping("/customer/delete/{id}")
    public Result<Void> deleteCustomer(@PathVariable("id") Long id) {
        // ERP 核心守则：绝不物理删除客户！我们用 is_deleted = 1 来做逻辑删除
        Customer c = new Customer();
        c.setId(id);
        c.setIsDeleted(1);
        customerMapper.update(c);
        return Result.success("客户已被作废屏蔽！", null);
    }

    // ========== 新增：供应商的 CRUD ==========

    @PostMapping("/supplier/add")
    public Result<Void> addSupplier(@RequestBody Supplier supplier) {
        supplierMapper.insert(supplier);
        return Result.success("新增供应商成功！", null);
    }

    @PutMapping("/supplier/update")
    public Result<Void> updateSupplier(@RequestBody Supplier supplier) {
        supplierMapper.update(supplier);
        return Result.success("修改供应商资料成功！", null);
    }

    @DeleteMapping("/supplier/delete/{id}")
    public Result<Void> deleteSupplier(@PathVariable("id") Long id) {
        Supplier s = new Supplier();
        s.setId(id);
        s.setIsDeleted(1);
        supplierMapper.update(s);
        return Result.success("供应商已被作废屏蔽！", null);
    }
}
