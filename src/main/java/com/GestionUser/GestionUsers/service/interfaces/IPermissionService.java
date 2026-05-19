package com.GestionUser.GestionUsers.service.interfaces;
import com.GestionUser.GestionUsers.dto.request.CreatePermissionRequest;
import com.GestionUser.GestionUsers.dto.response.PermissionResponse;
import java.util.List;
public interface IPermissionService {
    PermissionResponse create(CreatePermissionRequest req);
    PermissionResponse update(Long id, CreatePermissionRequest req);
    void delete(Long id);
    List<PermissionResponse> getAll();
}