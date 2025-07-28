package com.lms.services;

import com.lms.data.models.Role;
import com.lms.data.models.User;
import com.lms.data.respositories.UserRepository;
import com.lms.dto.requests.CreateInstructorRequestDTO;
import com.lms.dto.requests.UserRequestDTO;
import com.lms.dto.responses.UserResponse;
import com.lms.exception.EmailAlreadyExistsException;
import com.lms.exception.ResourceNotFoundException;
import com.lms.exception.UnauthorizedException;
import com.lms.security.CurrentUserProvider;
import com.lms.utils.EmailService;
import com.lms.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;
    private final EmailService emailService;


    @Override
    public UserResponse createUser(UserRequestDTO dto) {
        String email = dto.getEmail().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already in use: " + email);
        }

        dto.setEmail(email);
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        User user = UserMapper.toEntity(dto);
        User saved = userRepository.save(user);

        try {
            emailService.sendEmail(
                    saved.getEmail(),
                    "Welcome to the LMS!",
                    "Hi " + saved.getName() + ",\n\nWelcome to our Learning Management System. We're excited to have you on board!"
            );
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        return UserMapper.toDTO(saved);
    }

    @Override
    public UserResponse createInstructor(CreateInstructorRequestDTO dto) {
        User currentUser = currentUserProvider.getCurrentUser();

        if (!currentUser.getRole().equals(Role.SUPER_ADMIN)) {
            throw new UnauthorizedException("Only SUPER_ADMIN can create instructors.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User instructor = new User();
        instructor.setName(dto.getName());
        instructor.setEmail(dto.getEmail().toLowerCase());
        instructor.setPassword(passwordEncoder.encode(dto.getPassword()));
        instructor.setRole(Role.INSTRUCTOR);

        userRepository.save(instructor);

        try {
            emailService.sendEmail(
                    instructor.getEmail(),
                    "Instructor Account Created",
                    "Hi " + instructor.getName() + ",\n\nYour instructor account has been created. You can now start managing your courses."
            );
        } catch (Exception e) {
            System.err.println("Failed to send instructor email: " + e.getMessage());
        }

        return UserMapper.toDTO(instructor);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserMapper.toDTO(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> dtos = new ArrayList<>();

        for (User user : users) {
            dtos.add(UserMapper.toDTO(user));
        }

        return dtos;
    }

    @Override
    public UserResponse updateUser(UUID id, UserRequestDTO dto) {
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


    @Override
    public void deleteInstructor(UUID instructorId) {
        User currentUser = currentUserProvider.getCurrentUser();

        if (!currentUser.getRole().equals(Role.SUPER_ADMIN)) {
            throw new UnauthorizedException("Only SUPER_ADMIN can delete instructors.");
        }

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        if (!instructor.getRole().equals(Role.INSTRUCTOR)) {
            throw new IllegalArgumentException("The user is not an instructor.");
        }

        userRepository.delete(instructor);
    }

    @Override
    public List<UserResponse> getAllInstructors() {
        User currentUser = currentUserProvider.getCurrentUser();

        if (!(currentUser.getRole().equals(Role.SUPER_ADMIN) || currentUser.getRole().equals(Role.ADMIN))) {
            throw new UnauthorizedException("Only Admins and SuperAdmins can view instructors.");
        }

        List<User> instructors = userRepository.findByRole(Role.INSTRUCTOR);
        return instructors.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

}
