package com.lms.services;

import com.lms.data.models.User;
import com.lms.data.respositories.UserRepository;
import com.lms.dto.requests.UserRequestDTO;
import com.lms.dto.responses.UserResponseDTO;
import com.lms.exception.EmailAlreadyExistsException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        String email = dto.getEmail().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already in use: " + email);
        }

        dto.setEmail(email);
        dto.setPassword(passwordEncoder.encode(dto.getPassword())); //  encode here

        User user = UserMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return UserMapper.toDTO(saved);
    }


    @Override
    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserMapper.toDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> dtos = new ArrayList<>();

        for (User user : users) {
            dtos.add(UserMapper.toDTO(user));
        }

        return dtos;
    }

    @Override
    public UserResponseDTO updateUser(UUID id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));


        user.setName(dto.getName());
        String newEmail = dto.getEmail().toLowerCase();
        if (!user.getEmail().equals(newEmail)) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new EmailAlreadyExistsException("Email already in use: " + newEmail);
            }
            user.setEmail(newEmail);
        }
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User updated = userRepository.save(user);
        return UserMapper.toDTO(updated);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
}
