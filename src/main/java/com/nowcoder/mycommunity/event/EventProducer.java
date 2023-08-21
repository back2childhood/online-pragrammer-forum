package com.nowcoder.mycommunity.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.mycommunity.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // handle event
    public void fireEvent(Event event) {
        // send events to the specific topic
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
