package com.GestionUser.GestionUsers.mapper;
import com.GestionUser.GestionUsers.dto.response.UserResponse;
import com.GestionUser.GestionUsers.entity.User;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()),
            user.isEnabled(),
            user.getCreatedAt()
        );
    }
}