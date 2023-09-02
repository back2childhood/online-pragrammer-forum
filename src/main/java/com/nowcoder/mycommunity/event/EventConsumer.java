package com.nowcoder.mycommunity.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.mycommunity.entity.Event;
import com.nowcoder.mycommunity.entity.Message;
import com.nowcoder.mycommunity.service.MessageService;
import com.nowcoder.mycommunity.util.CommunityConstant;
import com.nowcoder.mycommunity.util.CommunityUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Component
public class EventConsumer implements CommunityConstant {

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${qiniu.key.accessKey}")
    private String accessKey;

    @Value("${qiniu.key.secretKey}")
    private String secretKey;

    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("the content of the message is empty");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("message format error");
            return;
        }

        // send in-station notification
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if(!event.getData().isEmpty()){
            content.putAll(event.getData());
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShareMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("the content of the message is empty");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("message format error");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCommand + " --quality 75 " + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;
        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("generate long image successfully: " + cmd);
        } catch (IOException e) {
            logger.info("generate long image fail: " + e.getMessage());
        }

        // start the timer to supervise whether the image was generated successfully
        // if there is a image, we should upload it to the cloud sever
        UploadImage uploadImage = new UploadImage(fileName, suffix);
        Future future = taskScheduler.scheduleAtFixedRate(uploadImage, 500);
        uploadImage.setFuture(future);
    }

    class UploadImage implements Runnable {

        // file name
        private String fileName;

        // file suffix
        private String fileSuffix;

        // the return value of this task
        private Future future;

        // start time
        private long startTime;

        // upload times
        private int uploadTimes;

        public void setFuture(Future future) {
            this.future = future;
        }

        public UploadImage(String fileName, String fileSuffix) {
            this.fileName = fileName;
            this.fileSuffix = fileSuffix;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            // timeout error
            if (System.currentTimeMillis() - startTime > 30000) {
                logger.error("The execution time is too long, the task is terminated: " + fileName);
                future.cancel(true);
                return;
            }
            // upload error
            if (uploadTimes > 3) {
                logger.error("The upload times is too many, the task is terminated: " + fileName);
                future.cancel(true);
                return;
            }

            String path = wkImageStorage + "/" + fileName + fileSuffix;
            File file = new File(path);
            if(file.exists()){
                logger.info(String.format("Start the %dth upload. [%s]", ++uploadTimes, fileName));
                // set the response info
                StringMap policy = new StringMap();
                policy.put("returnBody", CommunityUtil.getJSONString(0));
                // generate the upload certification
                Auth auth = Auth.create(accessKey, secretKey);
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);
                //
                UploadManager manager = new UploadManager(new Configuration(Zone.zone1()));
                try {
                    Response response = manager.put(
                            path, fileName, uploadToken, null, "image/" + fileSuffix.substring(fileSuffix.lastIndexOf(".") + 1), false);
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if(json == null || json.get("code") == null || !json.get("code").toString().equals("0")) {
                        logger.info(String.format("The %dth upload failed. [%s]", uploadTimes, fileName));
                    }else{
                        logger.info(String.format("The %dth upload success. [%s]", uploadTimes, fileName));
                        future.cancel(true);
                    }
                }catch (QiniuException e){
                    logger.info(String.format("The %dth upload failed. [%s]", uploadTimes, fileName));
                }
            }else{
                logger.info("Waiting for the image generated: " + fileName);
            }
        }
    }
}
