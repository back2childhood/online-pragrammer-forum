package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class ThreadPoolTest {

    private Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    // JDK's normal thread-pool
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // JDK's thread pool that periodically executes tasks
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    // Spring's normal thread pool
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    // Spring's thread pool that periodically executes tasks
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private AlphaService alphaService;

    private void sleep(long m){
        try{
            Thread.sleep(m);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.info("hello executor service");
            }
        };

        for(int i = 0; i < 10; i++) {
            executorService.submit(task);
        }

        sleep(10000);
    }

    @Test
    public void testScheduleExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.info("hello executor service");
            }
        };

        for(int i = 0; i < 10; i++) {
            scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);
        }

        sleep(30000);
    }

    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.info("hello spring thread pool");
            }
        };

        for(int i = 0; i < 10; i++){
            taskExecutor.submit(task);
        }

        sleep(10000);
    }

    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.info("hello spring thread pool");
            }
        };

        Date start = new Date(System.currentTimeMillis() + 10000);
//        for(int i = 0; i < 10; i++){
            taskScheduler.scheduleAtFixedRate(task, start, 1000);
//        }

        sleep(10000);
    }

    @Test
    public void testThreadPoolTaskExecutorSimple(){
//        for(int i=0; i< 10; ++ i){
//            alphaService.execute1();
//        }
        sleep(30000);
    }

    @Test
    public void testThreadPoolTaskExecutorSimple2(){
//        for(int i=0; i< 10; ++ i){
//            alphaService.execute2();
//        }
        sleep(30000);
    }
}
