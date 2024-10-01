package com.elabbasy.student.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RegisterUserRequest implements Serializable {
  private String email;

  private String password;

  private String fullName;
}
