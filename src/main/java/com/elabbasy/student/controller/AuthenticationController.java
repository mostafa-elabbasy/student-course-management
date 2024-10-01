package com.elabbasy.student.controller;

import com.elabbasy.student.model.entity.User;
import com.elabbasy.student.model.request.LoginUserRequest;
import com.elabbasy.student.model.request.RegisterUserRequest;
import com.elabbasy.student.model.response.LoginResponse;
import com.elabbasy.student.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping("/signup")
  public ResponseEntity<User> register(@RequestBody RegisterUserRequest userRequest) {
    return ResponseEntity.ok(authenticationService.signup(userRequest));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserRequest loginUserRequest) {
    return ResponseEntity.ok(authenticationService.authenticate(loginUserRequest));
  }
}
