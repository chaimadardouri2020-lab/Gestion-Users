package com.GestionUser.GestionUsers.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String firstName;
    @Column(nullable = false) private String lastName;
    @Column(unique = true, nullable = false) private String email;
    @Column(nullable = false) private String password;
    private boolean enabled = true;
    private boolean accountNonLocked = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
    public User() {}
    public static UserBuilder builder() { return new UserBuilder(); }
    public static class UserBuilder {
        private String firstName, lastName, email, password;
        private Set<Role> roles = new HashSet<>();
        private boolean enabled = true;
        public UserBuilder firstName(String v) { this.firstName = v; return this; }
        public UserBuilder lastName(String v) { this.lastName = v; return this; }
        public UserBuilder email(String v) { this.email = v; return this; }
        public UserBuilder password(String v) { this.password = v; return this; }
        public UserBuilder roles(Set<Role> v) { this.roles = v; return this; }
        public UserBuilder enabled(boolean v) { this.enabled = v; return this; }
        public User build() {
            User u = new User();
            u.firstName=firstName; u.lastName=lastName;
            u.email=email; u.password=password;
            u.roles=roles; u.enabled=enabled;
            return u;
        }
    }
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Set<Role> getRoles() { return roles; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setFirstName(String v) { this.firstName = v; }
    public void setLastName(String v) { this.lastName = v; }
    public void setEnabled(boolean v) { this.enabled = v; }
    public void setRoles(Set<Role> v) { this.roles = v; }
    public void setAccountNonLocked(boolean v) { this.accountNonLocked = v; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            role.getPermissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getName())));
        });
        return authorities;
    }
    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}