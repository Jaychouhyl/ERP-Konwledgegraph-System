package com.smartx.auth.controller;

import com.mybatisflex.core.paginate.Page;
import com.smartx.common.core.domain.Result;
import com.smartx.auth.domain.entity.Customer;
import com.smartx.auth.domain.entity.Supplier;
import com.smartx.auth.service.PartnerService;
import com.smartx.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth/partner")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private RedisService redisService;

    // 🌟 缓存 Key 规范
    private static final String SUPPLIER_CACHE_KEY = "auth:supplier:list";
    private static final String CUSTOMER_CACHE_KEY = "auth:customer:list";

    // ==================== 供应商接口 (Supplier) ====================

    @GetMapping("/supplier/list")
    public Result<List<Supplier>> listSuppliers() {
        List<Supplier> cachedList = (List<Supplier>) redisService.getCacheObject(SUPPLIER_CACHE_KEY);
        if (cachedList != null) {
            return Result.success("【起飞】走 Redis 缓存查询成功！", cachedList);
        }
        List<Supplier> list = partnerService.listAllSuppliers();
        redisService.setCacheObject(SUPPLIER_CACHE_KEY, list, 2, TimeUnit.HOURS);
        return Result.success("走 MySQL 数据库查询成功，并已存入缓存！", list);
    }

    @GetMapping("/supplier/page")
    public Result<Page<Supplier>> pageSupplier(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return Result.success("分页查询成功", partnerService.pageSupplier(pageNum, pageSize, keyword));
    }

    @GetMapping("/supplier/{id}")
    public Result<Supplier> getSupplierInfo(@PathVariable("id") Long id) {
        return Result.success("查询成功", partnerService.getSupplierById(id));
    }

    @PostMapping("/supplier/add")
    public Result<?> addSupplier(@RequestBody Supplier supplier) {
        if (partnerService.addSupplier(supplier)) {
            redisService.deleteObject(SUPPLIER_CACHE_KEY);
            return Result.success("新增供应商成功！", null);
        }
        return Result.fail("新增供应商失败");
    }

    @PutMapping("/supplier/update")
    public Result<?> updateSupplier(@RequestBody Supplier supplier) {
        if (partnerService.updateSupplier(supplier)) {
            redisService.deleteObject(SUPPLIER_CACHE_KEY);
            return Result.success("修改供应商成功！", null);
        }
        return Result.fail("修改供应商失败");
    }

    @DeleteMapping("/supplier/delete/{id}")
    public Result<?> deleteSupplier(@PathVariable("id") Long id) {
        if (partnerService.deleteSupplier(id)) {
            redisService.deleteObject(SUPPLIER_CACHE_KEY);
            return Result.success("供应商删除成功！", null);
        }
        return Result.fail("供应商删除失败");
    }


    // ==================== 客户接口 (Customer) ====================

    @GetMapping("/customer/list")
    public Result<List<Customer>> listCustomers() {
        List<Customer> cachedList = (List<Customer>) redisService.getCacheObject(CUSTOMER_CACHE_KEY);
        if (cachedList != null) {
            return Result.success("【起飞】走 Redis 缓存查询成功！", cachedList);
        }
        List<Customer> list = partnerService.listAllCustomers();
        redisService.setCacheObject(CUSTOMER_CACHE_KEY, list, 2, TimeUnit.HOURS);
        return Result.success("走 MySQL 数据库查询成功，并已存入缓存！", list);
    }

    @GetMapping("/customer/page")
    public Result<Page<Customer>> pageCustomer(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return Result.success("分页查询成功", partnerService.pageCustomer(pageNum, pageSize, keyword));
    }

    @GetMapping("/customer/{id}")
    public Result<Customer> getCustomerInfo(@PathVariable("id") Long id) {
        return Result.success("查询成功", partnerService.getCustomerById(id));
    }

    @PostMapping("/customer/add")
    public Result<?> addCustomer(@RequestBody Customer customer) {
        if (partnerService.addCustomer(customer)) {
            redisService.deleteObject(CUSTOMER_CACHE_KEY);
            return Result.success("新增客户成功！", null);
        }
        return Result.fail("新增客户失败");
    }

    @PutMapping("/customer/update")
    public Result<?> updateCustomer(@RequestBody Customer customer) {
        if (partnerService.updateCustomer(customer)) {
            redisService.deleteObject(CUSTOMER_CACHE_KEY);
            return Result.success("修改客户成功！", null);
        }
        return Result.fail("修改客户失败");
    }

    @DeleteMapping("/customer/delete/{id}")
    public Result<?> deleteCustomer(@PathVariable("id") Long id) {
        if (partnerService.deleteCustomer(id)) {
            redisService.deleteObject(CUSTOMER_CACHE_KEY);
            return Result.success("客户删除成功！", null);
        }
        return Result.fail("客户删除失败");
    }
}