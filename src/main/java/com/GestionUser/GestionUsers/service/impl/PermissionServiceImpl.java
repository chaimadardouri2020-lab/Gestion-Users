package com.GestionUser.GestionUsers.service.impl;
import com.GestionUser.GestionUsers.dto.request.CreatePermissionRequest;
import com.GestionUser.GestionUsers.dto.response.PermissionResponse;
import com.GestionUser.GestionUsers.entity.Permission;
import com.GestionUser.GestionUsers.exception.ResourceNotFoundException;
import com.GestionUser.GestionUsers.mapper.RoleMapper;
import com.GestionUser.GestionUsers.repository.PermissionRepository;
import com.GestionUser.GestionUsers.service.interfaces.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service @RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {
    private final PermissionRepository permissionRepository;
    @Override @Transactional
    public PermissionResponse create(CreatePermissionRequest req) {
        Permission p = new Permission(req.name(), req.description());
        permissionRepository.save(p);
        return toResponse(p);
    }
    @Override @Transactional
    public PermissionResponse update(Long id, CreatePermissionRequest req) {
        Permission p = permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        p.setName(req.name());
        p.setDescription(req.description());
        return toResponse(permissionRepository.save(p));
    }
    @Override @Transactional
    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) throw new ResourceNotFoundException("Permission not found");
        permissionRepository.deleteById(id);
    }
    @Override @Transactional(readOnly = true)
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }
    private PermissionResponse toResponse(Permission p) {
        return new PermissionResponse(p.getId(), p.getName(), p.getDescription(), p.getCreatedAt());
    }
}