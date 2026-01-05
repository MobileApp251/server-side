package mobile.jira.clonejira.scheduler;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.repository.ExpoNotiRepository;
import mobile.jira.clonejira.repository.TaskRepository;
import mobile.jira.clonejira.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SchedulingTasks {
    private final ExpoNotiRepository expoNotiRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Scheduled(cron = "0 * * * * *")
    public void checkDueTask() {
        Instant now = Instant.now();
//        Instant nextDay = new Date(now.toEpochMilli()).plusDays(1);
//        List<Task> dueTasks = taskRepository.findAllDueTasks();
    }
}
