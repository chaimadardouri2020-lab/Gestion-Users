package com.GestionUser.GestionUsers.service.impl;
import com.GestionUser.GestionUsers.dto.response.HistoriqueActionResponse;
import com.GestionUser.GestionUsers.dto.response.PagedResponse;
import com.GestionUser.GestionUsers.entity.HistoriqueAction;
import com.GestionUser.GestionUsers.entity.User;
import com.GestionUser.GestionUsers.mapper.HistoriqueMapper;
import com.GestionUser.GestionUsers.repository.HistoriqueActionRepository;
import com.GestionUser.GestionUsers.service.interfaces.IHistoriqueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class HistoriqueServiceImpl implements IHistoriqueService {
    private static final Logger log = LoggerFactory.getLogger(HistoriqueServiceImpl.class);
    private final HistoriqueActionRepository historiqueRepository;
    private final HistoriqueMapper historiqueMapper;
    @Override @Transactional
    public void log(User user, String action, String details, String ip, String entityType, Long entityId) {
        HistoriqueAction h = HistoriqueAction.builder()
            .user(user).action(action).details(details)
            .ipAddress(ip).entityType(entityType).entityId(entityId).build();
        historiqueRepository.save(h);
        log.info("Action logged: {} by {}", action, user != null ? user.getEmail() : "system");
    }
    @Override @Transactional(readOnly = true)
    public PagedResponse<HistoriqueActionResponse> getAll(int page, int size) {
        var p = historiqueRepository.findAll(PageRequest.of(page, size, Sort.by("date").descending()));
        return new PagedResponse<>(p.getContent().stream().map(historiqueMapper::toResponse).toList(),
            p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }
    @Override @Transactional(readOnly = true)
    public PagedResponse<HistoriqueActionResponse> getByUser(Long userId, int page, int size) {
        var p = historiqueRepository.findByUserId(userId, PageRequest.of(page, size, Sort.by("date").descending()));
        return new PagedResponse<>(p.getContent().stream().map(historiqueMapper::toResponse).toList(),
            p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }
}