package com.smartcycle.smartcycleapplication.services;

import com.smartcycle.smartcycleapplication.dtos.LoginRequestDTO;
import com.smartcycle.smartcycleapplication.dtos.RegistrationRequestDTO;
import com.smartcycle.smartcycleapplication.dtos.ResidentResponseDTO;
import com.smartcycle.smartcycleapplication.dtos.UserResponseDTO;
import com.smartcycle.smartcycleapplication.models.*;
import com.smartcycle.smartcycleapplication.repositories.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AuthService {

    // Dependencies from both registration and login
    private final UserRepository userRepository;
    private final ResidentRepository residentRepository;
    private final DriverRepository driverRepository;
    private final AdminRepository adminRepository;
    private final CollectionPersonnelRepository collectionPersonnelRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Unified constructor with all dependencies
    public AuthService(UserRepository userRepository,
                       ResidentRepository residentRepository,
                       DriverRepository driverRepository,
                       AdminRepository adminRepository,
                       CollectionPersonnelRepository collectionPersonnelRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.residentRepository = residentRepository;
        this.driverRepository = driverRepository;
        this.adminRepository = adminRepository;
        this.collectionPersonnelRepository = collectionPersonnelRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Handles new user registration.
     * @param request DTO with registration data.
     * @return A safe DTO of the created user.
     */
    public UserResponseDTO registerUser(RegistrationRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already in use. Please choose another.");
        }

        String role = request.getRole().toLowerCase();

        User savedUser = switch (role) {
            case "resident" -> createResident(request);
            case "driver" -> createDriver(request);
            case "admin" -> createAdmin(request);
            case "personnel" -> createCollectionPersonnel(request);
            default -> throw new IllegalArgumentException("Invalid user role specified: " + request.getRole());
        };

        return mapToUserResponseDTO(savedUser);
    }

    /**
     * Handles user login and authentication.
     * @param request DTO with login credentials.
     * @param response The HttpServletResponse to add the cookie to.
     * @return A map containing the JWT and the user data DTO.
     */
    public Map<String, Object> loginUser(LoginRequestDTO request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));

        String jwtToken = jwtService.generateToken(user);

        Cookie cookie = new Cookie("jwt", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        // cookie.setSecure(true); // Should be enabled in production with HTTPS
        response.addCookie(cookie);

        UserResponseDTO userResponse = mapToUserResponseDTO(user);

        return Map.of("token", jwtToken, "user", userResponse);
    }

    /**
     * Converts a User entity to a password-safe DTO for API responses.
     */
    private UserResponseDTO mapToUserResponseDTO(User user) {
        if (user instanceof Resident resident) {
            ResidentResponseDTO dto = new ResidentResponseDTO();
            dto.setId(resident.getId());
            dto.setEmail(resident.getEmail());
            dto.setName(resident.getName());
            dto.setAddress(resident.getAddress());
            dto.setAccountBalance(resident.getAccountBalance());
            dto.setRole("resident");
            return dto;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());

        if (user instanceof Driver) dto.setRole("driver");
        else if (user instanceof Admin) dto.setRole("admin");
        else if (user instanceof CollectionPersonnel) dto.setRole("personnel");

        return dto;
    }

    // --- Private Helper Methods for Registration ---

    private Resident createResident(RegistrationRequestDTO request) {
        if (request.getAddress() == null || request.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address is required for resident registration.");
        }
        Resident resident = new Resident();
        resident.setName(request.getName());
        resident.setEmail(request.getEmail());
        resident.setPassword(passwordEncoder.encode(request.getPassword()));
        resident.setAddress(request.getAddress());
        return residentRepository.save(resident);
    }

    private Driver createDriver(RegistrationRequestDTO request) {
        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setEmail(request.getEmail());
        driver.setPassword(passwordEncoder.encode(request.getPassword()));
        return driverRepository.save(driver);
    }

    private Admin createAdmin(RegistrationRequestDTO request) {
        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        return adminRepository.save(admin);
    }

    private CollectionPersonnel createCollectionPersonnel(RegistrationRequestDTO request) {
        CollectionPersonnel personnel = new CollectionPersonnel();
        personnel.setName(request.getName());
        personnel.setEmail(request.getEmail());
        personnel.setPassword(passwordEncoder.encode(request.getPassword()));
        personnel.setAssignedArea("Unassigned");
        return collectionPersonnelRepository.save(personnel);
    }
}