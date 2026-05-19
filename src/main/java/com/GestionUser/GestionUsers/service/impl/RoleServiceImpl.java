package com.GestionUser.GestionUsers.service.impl;
import com.GestionUser.GestionUsers.dto.request.CreateRoleRequest;
import com.GestionUser.GestionUsers.dto.response.RoleResponse;
import com.GestionUser.GestionUsers.entity.Permission;
import com.GestionUser.GestionUsers.entity.Role;
import com.GestionUser.GestionUsers.exception.ResourceNotFoundException;
import com.GestionUser.GestionUsers.mapper.RoleMapper;
import com.GestionUser.GestionUsers.repository.PermissionRepository;
import com.GestionUser.GestionUsers.repository.RoleRepository;
import com.GestionUser.GestionUsers.service.interfaces.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service @RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;
    @Override @Transactional
    public RoleResponse create(CreateRoleRequest req) {
        Role role = new Role(req.name(), req.description());
        if (req.permissionIds() != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(req.permissionIds()));
            role.setPermissions(permissions);
        }
        return roleMapper.toResponse(roleRepository.save(role));
    }
    @Override @Transactional
    public RoleResponse update(Long id, CreateRoleRequest req) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        role.setName(req.name());
        role.setDescription(req.description());
        if (req.permissionIds() != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(req.permissionIds()));
            role.setPermissions(permissions);
        }
        return roleMapper.toResponse(roleRepository.save(role));
    }
    @Override @Transactional
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) throw new ResourceNotFoundException("Role not found");
        roleRepository.deleteById(id);
    }
    @Override @Transactional(readOnly = true)
    public RoleResponse getById(Long id) {
        return roleMapper.toResponse(roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found")));
    }
    @Override @Transactional(readOnly = true)
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toResponse).collect(Collectors.toList());
    }
}