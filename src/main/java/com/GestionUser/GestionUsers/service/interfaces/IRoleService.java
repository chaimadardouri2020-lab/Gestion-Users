package com.GestionUser.GestionUsers.service.interfaces;
import com.GestionUser.GestionUsers.dto.request.CreateRoleRequest;
import com.GestionUser.GestionUsers.dto.response.RoleResponse;
import java.util.List;
public interface IRoleService {
    RoleResponse create(CreateRoleRequest req);
    RoleResponse update(Long id, CreateRoleRequest req);
    void delete(Long id);
    RoleResponse getById(Long id);
    List<RoleResponse> getAll();
}