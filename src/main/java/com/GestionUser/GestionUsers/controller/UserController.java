package com.GestionUser.GestionUsers.controller;
import com.GestionUser.GestionUsers.dto.request.UpdateUserRequest;
import com.GestionUser.GestionUsers.dto.response.ApiResponse;
import com.GestionUser.GestionUsers.service.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/users") @RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllUsers(
        @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers(page, size)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(id)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Mis a jour", userService.updateUser(id, req)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("Supprime", null));
    }
}