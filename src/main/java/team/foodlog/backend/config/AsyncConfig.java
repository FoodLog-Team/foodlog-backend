package team.foodlog.backend.config;

import java.time.Duration;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    public static final String USDA_REQUEST_EXECUTOR_BEAN_NAME = "usdaRequestExecutor";

    public static final String AI_REQUEST_EXECUTOR_BEAN_NAME = "aiRequestExecutor";

    @Bean(name = AI_REQUEST_EXECUTOR_BEAN_NAME)
    ThreadPoolTaskExecutor aiRequestExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return builder
                .corePoolSize(2)
                .maxPoolSize(2)
                .queueCapacity(10)
                .threadNamePrefix("ai-request-")
                .awaitTermination(true)
                .awaitTerminationPeriod(Duration.ofSeconds(30))
                .build();
    }

    @Bean(name = USDA_REQUEST_EXECUTOR_BEAN_NAME)
    ThreadPoolTaskExecutor usdaRequestExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return builder
                .corePoolSize(10)
                .maxPoolSize(20)
                .queueCapacity(100)
                .threadNamePrefix("usda-request-")
                .awaitTermination(true)
                .awaitTerminationPeriod(Duration.ofSeconds(10))
                .build();
    }

}
