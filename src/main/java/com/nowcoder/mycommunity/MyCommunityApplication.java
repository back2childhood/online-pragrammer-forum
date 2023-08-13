package com.nowcoder.mycommunity;

import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.nowcoder.mycommunity.dao"})
public class MyCommunityApplication {

    @PostConstruct
    public void init(){
        // fix the problem of netty startup conflicts
        // see Netty4Utils.setAvailableProcessors
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(MyCommunityApplication.class, args);
    }

}
