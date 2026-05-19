package com.GestionUser.GestionUsers.repository;
import com.GestionUser.GestionUsers.entity.HistoriqueAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long> {
    Page<HistoriqueAction> findByUserId(Long userId, Pageable pageable);
    Page<HistoriqueAction> findByAction(String action, Pageable pageable);
}