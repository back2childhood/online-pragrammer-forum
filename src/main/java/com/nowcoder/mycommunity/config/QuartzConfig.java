package com.nowcoder.mycommunity.config;

import com.nowcoder.mycommunity.quartz.AlphaJob;
import com.nowcoder.mycommunity.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// this configuration just for the first execution, and then the configuration will be saved in the database
@Configuration
public class QuartzConfig {

    // `FactoryBean` simplify the instantiation process of `Bean`
    // 1.The instantiation process of `Bean` is encapsulated through `FactoryBean`
    // 2.Assemble `FactoryBean` into `Spring`'s container
    // 3.Inject `FactoryBean` into the other beans
    // 4.This bean can acquire the object instance of the bean managed by the `FactoryBean`

    // inject `JobDetailFactoryBean` into this class
    // config `JobDetail`
//    @Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaGroup");
        // this task will be stored permanently although there are no triggers existing
        factoryBean.setDurability(true);
        // this task can be recovered after meeting some faults
        factoryBean.setRequestsRecovery(true);

        return factoryBean;
    }

    // the `JobDetail` used is the object instance in `JobDetailFactoryBean`
    // config `Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)`
//    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaGroup");
        // the interval of the trigger
        factoryBean.setRepeatInterval(3000);
        // use an object to store the status of the job, `JobDataMap()` is the default object
        factoryBean.setJobDataMap(new JobDataMap());

        return factoryBean;
    }

    // refresh the score of the post
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("PostScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        // this task will be stored permanently although there are no triggers existing
        factoryBean.setDurability(true);
        // this task can be recovered after meeting some faults
        factoryBean.setRequestsRecovery(true);

        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        // the interval of the trigger
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        // use an object to store the status of the job, `JobDataMap()` is the default object
        factoryBean.setJobDataMap(new JobDataMap());

        return factoryBean;
    }
}
