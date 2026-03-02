package com.smartx.auth.controller;

import com.smartx.auth.domain.entity.Customer;
import com.smartx.auth.domain.entity.Supplier;
import com.smartx.auth.mapper.CustomerMapper;
import com.smartx.auth.mapper.SupplierMapper;
import com.smartx.auth.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> listCustomers() {
        List<Customer> list = partnerService.getAllCustomers();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        result.put("msg", "success");
        return result;
    }

    @GetMapping("/suppliers")
    public Map<String, Object> listSuppliers() {
        List<Supplier> list = partnerService.getAllSuppliers();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        result.put("msg", "success");
        return result;
    }

    // ========== 新增：客户的 CRUD ==========

    @PostMapping("/customer/add")
    public Map<String, Object> addCustomer(@RequestBody Customer customer) {
        customerMapper.insert(customer);
        return success("新增客户成功！");
    }

    @PutMapping("/customer/update")
    public Map<String, Object> updateCustomer(@RequestBody Customer customer) {
        customerMapper.updateById(customer);
        return success("修改客户资料成功！");
    }

    @DeleteMapping("/customer/delete/{id}")
    public Map<String, Object> deleteCustomer(@PathVariable("id") Long id) {
        // ERP 核心守则：绝不物理删除客户！我们用 is_deleted = 1 来做逻辑删除
        Customer c = new Customer();
        c.setId(id);
        c.setIsDeleted(1);
        customerMapper.updateById(c);
        return success("客户已被作废屏蔽！");
    }

    // ========== 新增：供应商的 CRUD ==========

    @PostMapping("/supplier/add")
    public Map<String, Object> addSupplier(@RequestBody Supplier supplier) {
        supplierMapper.insert(supplier);
        return success("新增供应商成功！");
    }

    @PutMapping("/supplier/update")
    public Map<String, Object> updateSupplier(@RequestBody Supplier supplier) {
        supplierMapper.updateById(supplier);
        return success("修改供应商资料成功！");
    }

    @DeleteMapping("/supplier/delete/{id}")
    public Map<String, Object> deleteSupplier(@PathVariable("id") Long id) {
        Supplier s = new Supplier();
        s.setId(id);
        s.setIsDeleted(1);
        supplierMapper.updateById(s);
        return success("供应商已被作废屏蔽！");
    }

    // ========== 提取的通用返回方法 ==========
    private Map<String, Object> success(String msg) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", 0);
        res.put("msg", msg);
        return res;
    }
}
