package com.pcclub.Payment_Service.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.TaskScheduler;

@Configuration
public class SchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10); // Устанавливаем размер пула потоков (можно настроить)
        taskScheduler.setThreadNamePrefix("payment-task-");
        taskScheduler.initialize();
        return taskScheduler;
    }
}
