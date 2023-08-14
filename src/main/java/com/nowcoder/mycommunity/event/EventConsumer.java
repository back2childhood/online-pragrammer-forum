package com.nowcoder.mycommunity.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.mycommunity.entity.Event;
import com.nowcoder.mycommunity.entity.Message;
import com.nowcoder.mycommunity.service.MessageService;
import com.nowcoder.mycommunity.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.awt.event.MouseWheelEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

//    @Autowired
//    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
//
//    @Autowired
//    private MessageService messageService;
//
//    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
//    public void handleCommentMessage(ConsumerRecord record) {
//        if (record == null || record.value() == null) {
//            logger.error("the content of the message is empty");
//            return;
//        }
//
//        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
//        if (event == null) {
//            logger.error("message format error");
//            return;
//        }
//
//        // send in-station notification
//        Message message = new Message();
//        message.setFromId(SYSTEM_USER_ID);
//        message.setToId(event.getEntityUserId());
//        message.setConversationId(event.getTopic());
//        message.setCreateTime(new Date());
//
//        Map<String, Object> content = new HashMap<>();
//        content.put("userId", event.getUserId());
//        content.put("entityType", event.getEntityType());
//        content.put("entityId", event.getEntityId());
//
//        if(!event.getData().isEmpty()){
//            content.putAll(event.getData());
//        }
//
//        message.setContent(JSONObject.toJSONString(content));
//        messageService.addMessage(message);
//    }
}