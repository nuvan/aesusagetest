package cj.demo.aesusagetest.configuration;

import cj.demo.aesusagetest.service.RequestConfidentialInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Instant;
import java.util.Date;

import static java.time.temporal.ChronoUnit.SECONDS;

//http://stuartingram.com/2016/11/07/joy-and-pain-with-schedule-and-refreshscope-in-springboot-2/
@Configuration
@EnableScheduling
public class ScheduledCalls  implements SchedulingConfigurer {

    @Autowired
    RequestConfidentialInfoService confidentialInfoService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        confidentialInfoService.decryptMessage();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                triggerContext -> {
                    Instant nextTriggerTime = Instant.now().plus(5, SECONDS);
                    return Date.from(nextTriggerTime).toInstant();
                });
    }
}
