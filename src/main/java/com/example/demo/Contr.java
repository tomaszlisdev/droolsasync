package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Contr {
    @Autowired
    private AsyncManager manager;

    @GetMapping("run")
    public String run(){
        manager.makeInSeparateThread();
        return "ran";
    }

}
