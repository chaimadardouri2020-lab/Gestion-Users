package com.GestionUser.GestionUsers.controller;
import com.GestionUser.GestionUsers.dto.request.CreatePermissionRequest;
import com.GestionUser.GestionUsers.dto.response.ApiResponse;
import com.GestionUser.GestionUsers.service.interfaces.IPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/permissions") @RequiredArgsConstructor
public class PermissionController {
    private final IPermissionService permissionService;
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(permissionService.getAll()));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody CreatePermissionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Permission creee", permissionService.create(req)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @Valid @RequestBody CreatePermissionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Permission mise a jour", permissionService.update(id, req)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Permission supprimee", null));
    }
}