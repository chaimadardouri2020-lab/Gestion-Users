package com.GestionUser.GestionUsers.service.interfaces;
import com.GestionUser.GestionUsers.dto.response.HistoriqueActionResponse;
import com.GestionUser.GestionUsers.dto.response.PagedResponse;
import com.GestionUser.GestionUsers.entity.User;
public interface IHistoriqueService {
    void log(User user, String action, String details, String ip, String entityType, Long entityId);
    PagedResponse<HistoriqueActionResponse> getAll(int page, int size);
    PagedResponse<HistoriqueActionResponse> getByUser(Long userId, int page, int size);
}