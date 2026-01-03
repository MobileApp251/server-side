package mobile.jira.clonejira.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SchedulingTasks {
    @Scheduled(cron = "0 * * * * *")
    public void checkDueTask() {
        Instant now = Instant.now();
        System.out.print("checkDueTask >> ");
        System.out.println(now);
    }
}
