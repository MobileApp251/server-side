package mobile.jira.clonejira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // 1. Bắt buộc để kích hoạt @Async
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Cấu hình số lượng luồng
        executor.setCorePoolSize(5);     // Số luồng nòng cốt luôn chạy
        executor.setMaxPoolSize(10);     // Số luồng tối đa khi tải cao
        executor.setQueueCapacity(100);  // Số lượng task chờ trong hàng đợi
        executor.setThreadNamePrefix("MyAsyncThread-");
        executor.initialize();
        return executor;
    }
}