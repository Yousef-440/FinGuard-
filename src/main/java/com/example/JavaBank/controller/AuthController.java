package com.example.JavaBank.controller;

import com.example.JavaBank.dto.AuthResponseDto;
import com.example.JavaBank.dto.BankResponse;
import com.example.JavaBank.dto.LoginDto;
import com.example.JavaBank.dto.UserDto;
import com.example.JavaBank.repo.UserRepository;
import com.example.JavaBank.security.JWTGenerator;
import com.example.JavaBank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "User Account management APIs")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repository;
    @Autowired
    private JWTGenerator generator;
    @Autowired
    private UserService userService;

    @Operation(
            summary = "Login User By Email and Pass"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http status 200 OK"
    )

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail() , loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token =generator.generateToken(authentication);

        return new ResponseEntity<>(new AuthResponseDto(token) , HttpStatus.OK);
    }

    @Operation(
            summary = "Create New User Account"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http status 201 Created"
    )
    @PostMapping("/register")
    public ResponseEntity<BankResponse> register(@RequestBody UserDto userDto){
        return new ResponseEntity<>(userService.createAccount(userDto) , HttpStatus.CREATED);
    }
}
