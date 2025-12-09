package com.nutriscan.mpv.controllers;

import com.nutriscan.mpv.AuthConfig.JwtService;
import com.nutriscan.mpv.dto.LoginUserDto;
import com.nutriscan.mpv.dto.RegisterUserDto;
import com.nutriscan.mpv.dto.RegisterUserDtoResponse;
import com.nutriscan.mpv.dto.UserDtoLoginResponse;
import com.nutriscan.mpv.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationService authenticationService, JwtService jwtService, UserDetailsService userDetailsService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/signup")
    public ResponseEntity<RegisterUserDtoResponse> register(@RequestBody RegisterUserDto registerUserDto) {
        RegisterUserDtoResponse user = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDtoLoginResponse> login(@RequestBody LoginUserDto loginUserDto) {
        UserDtoLoginResponse user = authenticationService.authenticate(loginUserDto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<UserDtoLoginResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userName = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

            if(jwtService.isTokenValid(token, userDetails)) {
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
