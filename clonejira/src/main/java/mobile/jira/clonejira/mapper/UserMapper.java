package mobile.jira.clonejira.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import mobile.jira.clonejira.dto.UserDTO;
import mobile.jira.clonejira.entity.User;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(User user);

    @Mapping(target = "participates", ignore = true)
    User toEntity(UserDTO dto);
}
