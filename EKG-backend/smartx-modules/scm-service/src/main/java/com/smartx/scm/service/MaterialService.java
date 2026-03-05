package com.smartx.scm.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.smartx.scm.domain.entity.BaseMaterial;
import com.smartx.scm.mapper.BaseMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.smartx.scm.domain.entity.table.BaseMaterialTableDef.BASE_MATERIAL;

/**
 * 📦 物料主数据服务
 */
@Service
public class MaterialService {

    @Autowired
    private BaseMaterialMapper materialMapper;

    public boolean addMaterial(BaseMaterial material) {
        return materialMapper.insert(material) > 0;
    }

    public boolean updateMaterial(BaseMaterial material) {
        return materialMapper.update(material) > 0;
    }

    public boolean deleteMaterial(Long id) {
        return materialMapper.deleteById(id) > 0;
    }

    public BaseMaterial getById(Long id) {
        return materialMapper.selectOneById(id);
    }

    // 🌟 为你之前的 Redis 缓存提供全量查询支持
    public List<BaseMaterial> listAll() {
        return materialMapper.selectListByQuery(QueryWrapper.create());
    }

    // 🌟 为前端表格提供的极其优雅的分页搜索
    public Page<BaseMaterial> pageMaterial(int pageNum, int pageSize, String keyword) {
        QueryWrapper query = QueryWrapper.create();
        if (keyword != null && !keyword.isEmpty()) {
            query.where(BASE_MATERIAL.MATERIAL_NAME.like(keyword))
                    .or(BASE_MATERIAL.MATERIAL_CODE.like(keyword));
        }
        query.orderBy(BASE_MATERIAL.ID.desc());
        return materialMapper.paginate(pageNum, pageSize, query);
    }
}