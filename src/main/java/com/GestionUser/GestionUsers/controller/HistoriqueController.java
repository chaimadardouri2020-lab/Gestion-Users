package com.GestionUser.GestionUsers.controller;
import com.GestionUser.GestionUsers.dto.response.ApiResponse;
import com.GestionUser.GestionUsers.service.interfaces.IHistoriqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/historique") @RequiredArgsConstructor
public class HistoriqueController {
    private final IHistoriqueService historiqueService;
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(historiqueService.getAll(page, size)));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getByUser(
        @PathVariable Long userId,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(historiqueService.getByUser(userId, page, size)));
    }
}