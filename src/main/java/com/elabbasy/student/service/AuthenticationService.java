package com.elabbasy.student.service;

import com.elabbasy.student.exception.BusinessException;
import com.elabbasy.student.model.entity.User;
import com.elabbasy.student.model.request.LoginUserRequest;
import com.elabbasy.student.model.request.RegisterUserRequest;
import com.elabbasy.student.model.response.LoginResponse;
import com.elabbasy.student.repository.UserRepository;
import com.elabbasy.student.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public User signup(RegisterUserRequest userRequest) {
    if (userRepository.findByEmail(userRequest.getEmail()).isPresent()){
      throw new BusinessException("email already exist");
    }
    User user = User.builder()
      .fullName(userRequest.getFullName())
      .email(userRequest.getEmail())
      .password(passwordEncoder.encode(userRequest.getPassword())).build();

    return userRepository.save(user);
  }

  public LoginResponse authenticate(LoginUserRequest loginUserRequest) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        loginUserRequest.getEmail(),
        loginUserRequest.getPassword()
      )
    );

    User user = userRepository.findByEmail(loginUserRequest.getEmail())
      .orElseThrow();
    String token = jwtService.generateToken(user);
    return new LoginResponse(token, jwtService.getExpirationTime());
  }
}
