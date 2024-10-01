package com.elabbasy.student.mapper;

import com.elabbasy.student.model.dto.CourseDto;
import com.elabbasy.student.model.entity.Course;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {
  CourseDto toDto(Course course);
  Course toEntity(CourseDto courseDto);

  List<CourseDto> toDtoList(List<Course> courses);
}
