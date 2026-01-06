package mobile.jira.clonejira.scheduler;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.entity.Task;
import mobile.jira.clonejira.entity.key.ProjectTaskId;
import mobile.jira.clonejira.enums.NotifyType;
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
import java.util.UUID;

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
        List<Object[]> dueTasks = taskRepository.findAllDueTasks(now, nextDay);
        for (Object[] task : dueTasks) {
            ProjectTaskId id = (ProjectTaskId) task[0];
            UUID userId = (UUID) task[1];
            String code = (String) task[2];
            expoNotiService.sendPushNotification(
                    code,
                    "Task about to due!",
                    "Task #" + id.getProj_id() + "_" + id.getTask_id().toString(),
                    NotifyType.DUE_TASK,
                    userId.toString(), id.getProj_id(), id.getTask_id());
        }
        System.out.println("Due tasks have been sent: " + dueTasks.size());
    }
}
