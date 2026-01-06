package mobile.jira.clonejira.scheduler;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.mapper.NotificationMapper;
import mobile.jira.clonejira.repository.ExpoNotiRepository;
import mobile.jira.clonejira.repository.TaskRepository;
import mobile.jira.clonejira.repository.UserRepository;
import mobile.jira.clonejira.service.ExpoNotiService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SchedulingTasks {
    private final ExpoNotiRepository expoNotiRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ExpoNotiService expoNotiService;

    @Scheduled(cron = "5 * * * * *")
    public void checkDueTask() {
        Instant now = Instant.now();
        Instant nextDay = now.plus(1, ChronoUnit.DAYS);
        List<Task> dueTasks = taskRepository.findAllDueTasks(now, nextDay);
        for (Task task : dueTasks) {
            //expoNotiService.sendPushNotification();
        }
    }
}
