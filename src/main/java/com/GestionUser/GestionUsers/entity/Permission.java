package com.GestionUser.GestionUsers.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "permissions")
public class Permission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false) private String name;
    private String description;
    private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    public Permission() {}
    public Permission(String name, String description) { this.name = name; this.description = description; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setName(String v) { this.name = v; }
    public void setDescription(String v) { this.description = v; }
}