package com.elabbasy.student.model.dto;

import com.elabbasy.student.model.entity.Course;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseTimeDto implements Serializable {

  private UUID id;
  @NotNull(message = "start time is required")
  private LocalTime startTime;
  @NotNull(message = "end time is required")
  private LocalTime endTime;
  @NotNull(message = "day of week is required")
  private DayOfWeek dayOfWeek;
  @JsonIgnoreProperties("courseTimes")
  private Course course;
}
