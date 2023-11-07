package space.personal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig  {
    @Bean(name = "checkYoutubeLiveStream")
    public TaskExecutor checkYoutubeLiveStreamConfig() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 기본 스레드 수
        executor.setMaxPoolSize(8); // 최대 스레드 수
        executor.setQueueCapacity(10000); // 큐 크기
        executor.setThreadNamePrefix("checkYoutubeLiveStream");
        executor.initialize();
        return executor;
    }

    @Bean(name = "checkYoutubeLiveStreamInit")
    public TaskExecutor checkYoutubeLiveStreamInitConfig() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1); // 기본 스레드 수
        executor.setMaxPoolSize(1); // 최대 스레드 수
        executor.setQueueCapacity(10000); // 큐 크기
        executor.setThreadNamePrefix("checkYoutubeLiveStreamInit");
        executor.initialize();
        return executor;
    }
}
