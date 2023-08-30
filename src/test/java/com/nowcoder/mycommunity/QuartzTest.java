package com.nowcoder.mycommunity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class QuartzTest {

    @Autowired
    private Scheduler scheduler;

    @Test
    public void testDeleteJob(){
        try {
            boolean result = scheduler.deleteJobs(Collections.singletonList(new JobKey("alphaJob", "alphaGroup")));
            result = scheduler.deleteJobs(Collections.singletonList(new JobKey("PostScoreRefreshJob", "communityJobGroup")));
            System.out.println(result);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
