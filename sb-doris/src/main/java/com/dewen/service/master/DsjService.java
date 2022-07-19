package com.dewen.service.master;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dewen.entity.Dsj;
import com.dewen.mapper.DsjMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@DS("master")
public class DsjService extends ServiceImpl<DsjMapper, Dsj> {

    @DS("doris")
    public List<Dsj> listDsj() {
        return this.baseMapper.selectList(null);
    }

    @DS("doris")
    public void etl(List<Dsj> list) {
        for (Dsj dsj : list) {
            this.baseMapper.insert(dsj);
        }
    }

    @DS("doris")
    public IPage<Dsj> paging() {
        IPage<Dsj> page = this.baseMapper.selectPage(new Page<Dsj>(0, 10), new QueryWrapper<Dsj>());
        return page;
    }
}