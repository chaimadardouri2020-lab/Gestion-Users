package com.GestionUser.GestionUsers.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "roles")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false) private String name;
    private String description;
    private LocalDateTime createdAt;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    public Role() {}
    public Role(String name, String description) { this.name = name; this.description = description; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<Permission> getPermissions() { return permissions; }
    public void setName(String v) { this.name = v; }
    public void setDescription(String v) { this.description = v; }
    public void setPermissions(Set<Permission> v) { this.permissions = v; }
}