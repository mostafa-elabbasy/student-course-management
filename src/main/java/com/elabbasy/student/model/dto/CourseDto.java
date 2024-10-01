package com.elabbasy.student.model.dto;

import com.elabbasy.student.model.entity.CourseTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDto implements Serializable {

  private UUID id;
  @NotNull(message = "name is required")
  @NotEmpty(message = "name is required")
  private String name;
  @NotNull(message = "start date is required")
  private LocalDate startDate;
  @NotNull(message = "end date is required")
  private LocalDate endDate;
  @NotNull(message = "course times is required")
  @NotEmpty(message = "course times is required")
  @JsonIgnoreProperties("course")
  @Valid
  private Set<CourseTimeDto> courseTimes;

}
