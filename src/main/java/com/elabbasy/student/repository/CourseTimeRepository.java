package com.elabbasy.student.repository;

import com.elabbasy.student.model.entity.CourseTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseTimeRepository extends JpaRepository<CourseTime, UUID> {

    @Query(value = "select courseTime from CourseTime courseTime where courseTime.dayOfWeek = :dayOfWeek " +
      "and ((courseTime.startTime <= :startTime and courseTime.endTime > :startTime) " +
      "or (courseTime.startTime < :endTime and courseTime.endTime >= :endTime) " +
      "or (courseTime.startTime >= :startTime and courseTime.endTime <= :endTime))" +
      "and ((courseTime.course.startDate <= :startDate and courseTime.course.endDate >= :startDate)" +
      "or (courseTime.course.startDate <= :endDate and courseTime.course.endDate >= :endDate)" +
      "or (courseTime.course.startDate >= :startDate and courseTime.course.endDate <= :endDate))")
    Optional<CourseTime> findByDayOfWeekAndStartTimeAndEndTime(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate);
}
