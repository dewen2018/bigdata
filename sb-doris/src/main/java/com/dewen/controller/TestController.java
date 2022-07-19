package com.dewen.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dewen.entity.Dsj;
import com.dewen.service.master.DsjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private DsjService dsjService;

    @GetMapping("/list")
    public List<Dsj> list() {
        return dsjService.list();
    }

    @GetMapping("/listDsj")
    public List<Dsj> listDsj() {
        return dsjService.listDsj();
    }

    @GetMapping("/etl")
    public void etl() {
        List<Dsj> list = dsjService.list();
        dsjService.etl(list);
    }

    @GetMapping("/paging")
    public IPage<Dsj> paging() {
        return dsjService.paging();
    }
}
