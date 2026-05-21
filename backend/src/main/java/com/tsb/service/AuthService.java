package com.tsb.service;

import com.tsb.dto.AuthRequest;
import com.tsb.dto.AuthResponse;
import com.tsb.dto.RegisterRequest;
import com.tsb.model.Role;
import com.tsb.model.User;
import com.tsb.repository.UserRepository;
import com.tsb.security.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

}
