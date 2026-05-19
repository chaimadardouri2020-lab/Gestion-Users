package com.GestionUser.GestionUsers.config;
import com.GestionUser.GestionUsers.entity.Permission;
import com.GestionUser.GestionUsers.entity.Role;
import com.GestionUser.GestionUsers.entity.User;
import com.GestionUser.GestionUsers.repository.PermissionRepository;
import com.GestionUser.GestionUsers.repository.RoleRepository;
import com.GestionUser.GestionUsers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;
@Component @RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;
        Permission pRead = permissionRepository.save(new Permission("READ", "Lire les donnees"));
        Permission pWrite = permissionRepository.save(new Permission("WRITE", "Ecrire les donnees"));
        Permission pDelete = permissionRepository.save(new Permission("DELETE", "Supprimer les donnees"));
        Permission pAdmin = permissionRepository.save(new Permission("ADMIN_ACCESS", "Acces administration"));
        Role roleViewer = new Role("ROLE_VIEWER", "Lecteur seul");
        roleViewer.setPermissions(Set.of(pRead));
        roleRepository.save(roleViewer);
        Role roleUser = new Role("ROLE_USER", "Utilisateur standard");
        roleUser.setPermissions(Set.of(pRead, pWrite));
        roleRepository.save(roleUser);
        Role roleManager = new Role("ROLE_MANAGER", "Manager");
        roleManager.setPermissions(Set.of(pRead, pWrite, pDelete));
        roleRepository.save(roleManager);
        Role roleAdmin = new Role("ROLE_ADMIN", "Administrateur");
        roleAdmin.setPermissions(Set.of(pRead, pWrite, pDelete, pAdmin));
        roleRepository.save(roleAdmin);
        Role roleSuperAdmin = new Role("ROLE_SUPER_ADMIN", "Super Administrateur");
        roleSuperAdmin.setPermissions(Set.of(pRead, pWrite, pDelete, pAdmin));
        roleRepository.save(roleSuperAdmin);
        User admin = User.builder()
            .firstName("Super").lastName("Admin")
            .email("admin@test.com")
            .password(passwordEncoder.encode("Admin@123!"))
            .roles(Set.of(roleAdmin)).build();
        userRepository.save(admin);
        User manager = User.builder()
            .firstName("Manager").lastName("Test")
            .email("manager@test.com")
            .password(passwordEncoder.encode("Manager@123!"))
            .roles(Set.of(roleManager)).build();
        userRepository.save(manager);
        User user = User.builder()
            .firstName("User").lastName("Test")
            .email("user@test.com")
            .password(passwordEncoder.encode("User@123!"))
            .roles(Set.of(roleUser)).build();
        userRepository.save(user);
        log.info("Data initialized: 5 roles, 4 permissions, 3 users");
    }
}