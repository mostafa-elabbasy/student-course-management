package com.elabbasy.student.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "course_times")
@Entity
@Builder
public class CourseTime implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false)
  private UUID id;
  @Column(nullable = false)
  private LocalTime startTime;
  @Column(nullable = false)
  private LocalTime endTime;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DayOfWeek dayOfWeek;
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "course_id")
  @JsonIgnoreProperties({"courseTimes"})
  private Course course;
}
