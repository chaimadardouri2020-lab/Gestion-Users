package com.GestionUser.GestionUsers.controller;
import com.GestionUser.GestionUsers.dto.request.CreateRoleRequest;
import com.GestionUser.GestionUsers.dto.response.ApiResponse;
import com.GestionUser.GestionUsers.service.interfaces.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/roles") @RequiredArgsConstructor
public class RoleController {
    private final IRoleService roleService;
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(roleService.getAll()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(roleService.getById(id)));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody CreateRoleRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Role cree", roleService.create(req)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @Valid @RequestBody CreateRoleRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Role mis a jour", roleService.update(id, req)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Role supprime", null));
    }
}