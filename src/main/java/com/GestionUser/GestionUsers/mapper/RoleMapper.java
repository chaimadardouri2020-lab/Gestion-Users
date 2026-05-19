package com.GestionUser.GestionUsers.mapper;
import com.GestionUser.GestionUsers.dto.response.PermissionResponse;
import com.GestionUser.GestionUsers.dto.response.RoleResponse;
import com.GestionUser.GestionUsers.entity.Role;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
@Component
public class RoleMapper {
    public RoleResponse toResponse(Role role) {
        return new RoleResponse(
            role.getId(), role.getName(), role.getDescription(),
            role.getPermissions().stream().map(p -> new PermissionResponse(p.getId(), p.getName(), p.getDescription(), p.getCreatedAt())).collect(Collectors.toSet()),
            role.getCreatedAt()
        );
    }
}