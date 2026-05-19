package com.GestionUser.GestionUsers.mapper;
import com.GestionUser.GestionUsers.dto.response.HistoriqueActionResponse;
import com.GestionUser.GestionUsers.entity.HistoriqueAction;
import org.springframework.stereotype.Component;
@Component
public class HistoriqueMapper {
    public HistoriqueActionResponse toResponse(HistoriqueAction h) {
        return new HistoriqueActionResponse(
            h.getId(),
            h.getUser() != null ? h.getUser().getEmail() : "system",
            h.getAction(), h.getDetails(), h.getIpAddress(),
            h.getEntityType(), h.getEntityId(), h.getDate()
        );
    }
}