package com.elabbasy.student.service;

import com.elabbasy.student.exception.BusinessException;
import com.elabbasy.student.mapper.CourseMapper;
import com.elabbasy.student.model.dto.CourseDto;
import com.elabbasy.student.model.dto.CourseTimeDto;
import com.elabbasy.student.model.entity.Course;
import com.elabbasy.student.model.entity.CourseTime;
import com.elabbasy.student.repository.CourseRepository;
import com.elabbasy.student.repository.CourseTimeRepository;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
  @Mock
  private CourseRepository courseRepository;
  @Mock
  private CourseMapper courseMapper;
  @Mock
  private CourseTimeRepository courseTimeRepository;
  @InjectMocks
  private CourseService courseService;

  @Test
  public void saveCourseTest() {
    Course course = new Course(null, "course 1", LocalDate.parse("2024-01-12"), LocalDate.parse("2025-12-30"), Set.of(new CourseTime(null, LocalTime.of(10, 0), LocalTime.of(12, 0), DayOfWeek.MONDAY, null)));
    CourseDto courseDto = new CourseDto(UUID.randomUUID(), "course 1", LocalDate.parse("2024-01-12"), LocalDate.parse("2025-12-30"), Set.of(new CourseTimeDto(UUID.randomUUID(), LocalTime.of(10, 0), LocalTime.of(12, 0), DayOfWeek.MONDAY, null)));

    when(courseRepository.save(ArgumentMatchers.any(Course.class))).thenReturn(course);
    given(courseMapper.toDto(ArgumentMatchers.any())).willReturn(courseDto);
    given(courseMapper.toEntity(ArgumentMatchers.any())).willReturn(course);
    when(courseTimeRepository.findByDayOfWeekAndStartTimeAndEndTime(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.empty());

    CourseDto created = courseService.registerCourse(courseDto);

    assertThat(created.getName()).isSameAs(courseDto.getName());
    assertThat(created.getId()).isSameAs(courseDto.getId());

    verify(courseRepository).save(course);
  }

  @Test
  public void saveCourseWithExistTimeSlotTest() {
    Course course = new Course(null, "course 1", LocalDate.parse("2024-01-12"), LocalDate.parse("2025-12-30"), Set.of(new CourseTime(null, LocalTime.of(10, 0), LocalTime.of(12, 0), DayOfWeek.MONDAY, null)));
    CourseDto courseDto = new CourseDto(UUID.randomUUID(), "course 1", LocalDate.parse("2024-01-12"), LocalDate.parse("2025-12-30"), Set.of(new CourseTimeDto(UUID.randomUUID(), LocalTime.of(10, 0), LocalTime.of(12, 0), DayOfWeek.MONDAY, null)));

    when(courseTimeRepository.findByDayOfWeekAndStartTimeAndEndTime(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.of(new CourseTime()));

    assertThrows(BusinessException.class, () -> courseService.registerCourse(courseDto));
  }

  @Test
  void getAllCoursesTest() {
    Course course1 = new Course(UUID.randomUUID(), "course 1", LocalDate.parse("2024-01-12"), LocalDate.parse("2025-12-30"), Set.of(new CourseTime(UUID.randomUUID(), LocalTime.of(10, 0), LocalTime.of(12, 0), DayOfWeek.MONDAY, null)));
    Course course2 = new Course(UUID.randomUUID(), "course 2", LocalDate.parse("2026-01-12"), LocalDate.parse("2027-12-30"), Set.of(new CourseTime(UUID.randomUUID(), LocalTime.of(10, 0), LocalTime.of(12, 0), DayOfWeek.MONDAY, null)));

    CourseDto courseDto1 = new CourseDto(UUID.randomUUID(), "course 1", LocalDate.parse("2024-01-12"), LocalDate.parse("2025-12-30"), Set.of(new CourseTimeDto(UUID.randomUUID(), LocalTime.of(10, 0), LocalTime.of(12, 0), DayOfWeek.MONDAY, null)));
    CourseDto courseDto2 = new CourseDto(UUID.randomUUID(), "course 2", LocalDate.parse("2026-01-12"), LocalDate.parse("2027-12-30"), Set.of(new CourseTimeDto(UUID.randomUUID(), LocalTime.of(10, 0), LocalTime.of(12, 0), DayOfWeek.MONDAY, null)));

    given(courseRepository.findAll())
      .willReturn(List.of(course1, course2));
    given(courseMapper.toDtoList(List.of(course1, course2)))
      .willReturn(List.of(courseDto1, courseDto2));
    var coursesList = courseService.list();

    assertThat(coursesList).isNotNull();
    assertThat(coursesList.size()).isEqualTo(2);

  }

  @Test
  void cancelCourseTest() {
    Course course = new Course(UUID.randomUUID(), "course 1", LocalDate.parse("2024-01-12"), LocalDate.parse("2025-12-30"), Set.of(new CourseTime(UUID.randomUUID(), LocalTime.of(10, 0), LocalTime.of(12, 0), DayOfWeek.MONDAY, null)));
    given(courseRepository.findById(ArgumentMatchers.any()))
      .willReturn(Optional.of(course));

    Mockito.doNothing().when(this.courseRepository).delete(Mockito.any(Course.class));
    this.courseService.cancelCourse(course.getId());

    Mockito.verify(this.courseRepository, Mockito.times(1))
      .delete(Mockito.any(Course.class));
  }

  @Test
  void cancelCourseTestNotFound() {
    assertThrows(BusinessException.class, () -> courseService.cancelCourse(UUID.randomUUID()));
  }

  @Test
  void getCourseSchedulePdfTest() throws DocumentException {
    Course course = new Course(UUID.randomUUID(), "course 1", LocalDate.parse("2024-01-12"), LocalDate.parse("2025-12-30"), Set.of(new CourseTime(UUID.randomUUID(), LocalTime.of(10, 0), LocalTime.of(12, 0), DayOfWeek.MONDAY, null)));

    given(courseRepository.findById(ArgumentMatchers.any()))
      .willReturn(Optional.of(course));

    ByteArrayOutputStream outputStream = courseService.scheduleCourseAsPdf(course.getId());

    assertThat(outputStream).isNotNull();
    assertThat(outputStream.size()).isGreaterThan(0);
  }

  @Test
  void getCourseSchedulePdfNotFoundTest() {
    assertThrows(BusinessException.class, () -> courseService.cancelCourse(UUID.randomUUID()));
  }

}
