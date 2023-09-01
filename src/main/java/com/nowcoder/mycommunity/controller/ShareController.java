package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.entity.Event;
import com.nowcoder.mycommunity.event.EventProducer;
import com.nowcoder.mycommunity.util.CommunityConstant;
import com.nowcoder.mycommunity.util.CommunityUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ShareController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private EventProducer eventProducer;

    @Value("${mycommunity.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @GetMapping(path = "/share")
    @ResponseBody
    public String share(String htmlUrl){
        // generate the file name
        String fileName = CommunityUtil.generateUUID();

        // Asynchronous generation long pic
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("fileName", fileName)
                .setData("suffix", ".png");
        eventProducer.fireEvent(event);

        Map<String, Object> map = new HashMap<>();
        map.put("shareUrl", domain + contextPath + "/share/image" + fileName);

        return CommunityUtil.getJSONString(0, null, map);
    }

    //
    @GetMapping(path = "/share/image/{fileName}")
    public void getShareImage(@PathVariable("fileName") String fileName, HttpServletResponse response){
        if(StringUtils.isBlank(fileName)){
            throw new IllegalStateException("the file name cannot be blank");
        }

        response.setContentType("image/png");
        File file = new File(wkImageStorage + "/" + fileName + ".png");
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[1024];
            int len = 0;
            while ((len = fis.read(data)) != -1) {
                os.write(data, 0, len);
            }
        } catch (IOException e) {
            logger.error("querty the long image failed: ", e.getMessage());
        }
    }
}
