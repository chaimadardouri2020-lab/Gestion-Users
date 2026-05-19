package com.GestionUser.GestionUsers.service.interfaces;
import com.GestionUser.GestionUsers.dto.request.UpdateUserRequest;
import com.GestionUser.GestionUsers.dto.response.PagedResponse;
import com.GestionUser.GestionUsers.dto.response.UserResponse;
public interface IUserService {
    PagedResponse<UserResponse> getAllUsers(int page, int size);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UpdateUserRequest req);
    void deleteUser(Long id);
}