package agh.edu.pl.healthmonitoringsystemai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Minimum number of threads in the pool
        executor.setMaxPoolSize(50); // Maximum number of threads in the pool
        executor.setQueueCapacity(100); // Queue capacity when threads are fully utilized
        executor.setKeepAliveSeconds(60); // Time of live unused threads
        executor.setThreadNamePrefix("AsyncThread-"); // Prefix for thread names
        executor.setAllowCoreThreadTimeOut(true);
        executor.initialize();
        return executor;
    }
}
