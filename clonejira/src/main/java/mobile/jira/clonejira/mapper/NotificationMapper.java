package mobile.jira.clonejira.mapper;

import mobile.jira.clonejira.dto.notification.NotificationDTO;
import mobile.jira.clonejira.entity.Notifications;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    NotificationDTO toDTO(Notifications notification);

    Notifications toEntity(NotificationDTO dto);
}
