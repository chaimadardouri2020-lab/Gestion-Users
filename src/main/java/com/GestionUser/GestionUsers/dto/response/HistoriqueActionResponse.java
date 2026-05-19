package com.GestionUser.GestionUsers.dto.response;
import java.time.LocalDateTime;
public record HistoriqueActionResponse(Long id, String userEmail, String action, String details, String ipAddress, String entityType, Long entityId, LocalDateTime date) {}