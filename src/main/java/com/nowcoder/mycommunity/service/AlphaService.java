package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.dao.AlphaDao;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public String find(){
        return alphaDao.select();
    }

    @PostConstruct
    public void init(){
        System.out.println("initiative");
    }
}
