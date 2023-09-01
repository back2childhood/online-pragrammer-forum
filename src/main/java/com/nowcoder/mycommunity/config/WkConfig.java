package com.nowcoder.mycommunity.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class WkConfig {

    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @PostConstruct
    public void init(){
        // create the dic about WKimage
        File file = new File(wkImageStorage);
        if(!file.exists()){
            file.mkdir();
            logger.info("create the dictionary of WKimage: " + wkImageStorage);
        }

    }
}
