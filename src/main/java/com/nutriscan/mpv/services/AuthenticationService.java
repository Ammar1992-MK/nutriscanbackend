package com.nutriscan.mpv.services;

import com.nutriscan.mpv.AuthConfig.JwtService;
import com.nutriscan.mpv.NutritionProfile;
import com.nutriscan.mpv.User;
import com.nutriscan.mpv.dto.*;
import com.nutriscan.mpv.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterUserDtoResponse signup(RegisterUserDto registerUserDto) {
        if (userRepository.existsByEmail(registerUserDto.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        User user = new User();
        user.setFullName(registerUserDto.fullName());
        user.setEmail(registerUserDto.email());
        user.setPassword(passwordEncoder.encode(registerUserDto.password()));
        user.setCountry(registerUserDto.country());
        user.setPhoneNumber(registerUserDto.phoneNumber());

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new RegisterUserDtoResponse(user.getFullName(), user.getEmail(), user.getCountry(), user.getPhoneNumber(), accessToken, refreshToken);
    }

    public UserDtoLoginResponse authenticate(LoginUserDto loginUserDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.email(), loginUserDto.password())
        );
        User user = userRepository.findByEmail(loginUserDto.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        NutritionProfileDto nutritionProfileDto = getUserNutritionProfile(user);
        return new UserDtoLoginResponse(user.getFullName(), user.getEmail(), user.getPhoneNumber(), user.getCountry(), nutritionProfileDto, accessToken, refreshToken);
    }

    public String refreshAccessToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!refreshToken.equals(user.getRefreshToken()) || !jwtService.isTokenValid(refreshToken, user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        return jwtService.generateToken(user);
    }

    private NutritionProfileDto getUserNutritionProfile(User user) {
        NutritionProfile nutritionProfile = user.getNutritionProfile();
        if (nutritionProfile == null) return null;
        return new NutritionProfileDto(nutritionProfile.getGoal(), nutritionProfile.getAllergy(), nutritionProfile.getDiet(), nutritionProfile.getOtherPreferences());
    }
}
