package com.hotel.bookingsystem.controller;

import com.hotel.bookingsystem.dto.RegisterRequest;
import com.hotel.bookingsystem.dto.UserDTO;
import com.hotel.bookingsystem.model.entity.User;
import com.hotel.bookingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // TODO: Encrypt later
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        
        User savedUser = userService.registerUser(user);
        
        UserDTO response = new UserDTO(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole()
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        
        List<UserDTO> userDTOs = users.stream()
            .map(user -> new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDTOs);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        UserDTO userDTO = new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
        
        return ResponseEntity.ok(userDTO);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
