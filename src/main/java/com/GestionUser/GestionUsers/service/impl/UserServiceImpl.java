package com.GestionUser.GestionUsers.service.impl;
import com.GestionUser.GestionUsers.dto.request.UpdateUserRequest;
import com.GestionUser.GestionUsers.dto.response.PagedResponse;
import com.GestionUser.GestionUsers.dto.response.UserResponse;
import com.GestionUser.GestionUsers.entity.Role;
import com.GestionUser.GestionUsers.entity.User;
import com.GestionUser.GestionUsers.exception.ResourceNotFoundException;
import com.GestionUser.GestionUsers.mapper.UserMapper;
import com.GestionUser.GestionUsers.repository.RoleRepository;
import com.GestionUser.GestionUsers.repository.UserRepository;
import com.GestionUser.GestionUsers.service.interfaces.IHistoriqueService;
import com.GestionUser.GestionUsers.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;
@Service @RequiredArgsConstructor
public class UserServiceImpl implements IUserService, UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final IHistoriqueService historiqueService;
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
    }
    @Override @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(int page, int size) {
        var p = userRepository.findAll(PageRequest.of(page, size));
        return new PagedResponse<>(p.getContent().stream().map(userMapper::toResponse).toList(),
            p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }
    @Override @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id)));
    }
    @Override @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        if (req.firstName() != null) user.setFirstName(req.firstName());
        if (req.lastName() != null) user.setLastName(req.lastName());
        if (req.role() != null) {
            String roleName = req.role().toUpperCase().startsWith("ROLE_") ? req.role().toUpperCase() : "ROLE_" + req.role().toUpperCase();
            Role role = roleRepository.findByName(roleName).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        }
        historiqueService.log(user, "UPDATE_USER", "Utilisateur mis a jour", null, "User", id);
        return userMapper.toResponse(userRepository.save(user));
    }
    @Override @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        historiqueService.log(user, "DELETE_USER", "Utilisateur supprime", null, "User", id);
        userRepository.deleteById(id);
        log.info("User deleted: {}", id);
    }
}