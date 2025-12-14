package mobile.jira.clonejira.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import mobile.jira.clonejira.dto.UserDTO;
import mobile.jira.clonejira.entity.User;

// componentModel = "spring" giúp bạn có thể @Autowired UserMapper vào Service
@Mapper(componentModel = "spring")
@Component
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // MapStruct tự biết convert UUID <-> String
    UserDTO toDTO(User user);

    // Map UserDTO sang User
    User toEntity(UserDTO dto);
}
