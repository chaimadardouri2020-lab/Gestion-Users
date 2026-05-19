package com.GestionUser.GestionUsers.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "historique_actions")
public class HistoriqueAction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") private User user;
    @Column(nullable = false) private String action;
    private String details;
    private String ipAddress;
    private String entityType;
    private Long entityId;
    @Column(nullable = false) private LocalDateTime date;
    @PrePersist protected void onCreate() { date = LocalDateTime.now(); }
    public HistoriqueAction() {}
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private User user;
        private String action, details, ipAddress, entityType;
        private Long entityId;
        public Builder user(User v) { this.user = v; return this; }
        public Builder action(String v) { this.action = v; return this; }
        public Builder details(String v) { this.details = v; return this; }
        public Builder ipAddress(String v) { this.ipAddress = v; return this; }
        public Builder entityType(String v) { this.entityType = v; return this; }
        public Builder entityId(Long v) { this.entityId = v; return this; }
        public HistoriqueAction build() {
            HistoriqueAction h = new HistoriqueAction();
            h.user=user; h.action=action; h.details=details;
            h.ipAddress=ipAddress; h.entityType=entityType; h.entityId=entityId;
            return h;
        }
    }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public String getIpAddress() { return ipAddress; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public LocalDateTime getDate() { return date; }
}