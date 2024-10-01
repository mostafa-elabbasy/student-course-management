package com.elabbasy.student.service;

import com.elabbasy.student.exception.BusinessException;
import com.elabbasy.student.mapper.CourseMapper;
import com.elabbasy.student.model.dto.CourseDto;
import com.elabbasy.student.model.entity.Course;
import com.elabbasy.student.model.entity.CourseTime;
import com.elabbasy.student.model.response.Session;
import com.elabbasy.student.repository.CourseRepository;
import com.elabbasy.student.repository.CourseTimeRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CourseService {
  private final CourseRepository courseRepository;
  private final CourseTimeRepository courseTimeRepository;
  private final CourseMapper courseMapper;
  public CourseDto registerCourse(CourseDto courseDto){
    courseDto.getCourseTimes().forEach(courseTime -> {
      Optional<CourseTime> checkTimeValid = courseTimeRepository.findByDayOfWeekAndStartTimeAndEndTime(courseTime.getDayOfWeek(), courseTime.getStartTime(), courseTime.getEndTime(), courseDto.getStartDate(), courseDto.getEndDate());
      if (checkTimeValid.isPresent())
        throw new BusinessException("Course time already reserved");
    });

    Course course = courseMapper.toEntity(courseDto);
    course.getCourseTimes().forEach(courseTime -> {
      courseTime.setCourse(course);
    });
    Course result = courseRepository.save(course);

    return courseMapper.toDto(result);
  }
  public void cancelCourse(UUID id){
    Course course = courseRepository.findById(id).orElseThrow(() -> new BusinessException("course not found"));
    courseRepository.delete(course);
  }
  @Cacheable("courses")
  public List<CourseDto> list(){
    List<Course> courses = courseRepository.findAll();
    return courseMapper.toDtoList(courses);
  }
  public ByteArrayOutputStream scheduleCourseAsPdf(UUID id) throws DocumentException {
    Course course = courseRepository.findById(id).orElseThrow(() -> new BusinessException("course not found"));
    List<Session> sessions = new ArrayList<>();
    course.getCourseTimes().forEach(courseTime -> {
      List<LocalDate> sessionDates = getSessionsDates(course.getStartDate(), course.getEndDate(), courseTime.getDayOfWeek());
      sessionDates.forEach(date -> {
        Session session = new Session(date, courseTime.getStartTime(), courseTime.getEndTime());
        sessions.add(session);
      });
    });
    List<Session> sortedSessions = sessions.stream()
      .sorted(Comparator.comparing(Session::getDate))
      .collect(Collectors.toList());

    Document document = new Document();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, outputStream);

    document.open();

    PdfPTable table = new PdfPTable(3);
    addTableHeader(table);
    addRows(table, sortedSessions);

    document.add(table);
    document.close();


    return outputStream;
  }
  private void addTableHeader(PdfPTable table) {
    Stream.of("Session Date", "Start Time", "End Time")
      .forEach(columnTitle -> {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase(columnTitle));
        table.addCell(header);
      });
  }
  private void addRows(PdfPTable table, List<Session> sessions) {
    sessions.forEach(session -> {
      table.addCell(session.getDate().format(DateTimeFormatter.ofPattern("EEEE dd-MM-yyyy")));
      table.addCell(session.getStartTime().toString());
      table.addCell(session.getEndTime().toString());
    });
  }
  private List<LocalDate> getSessionsDates(LocalDate start, LocalDate end, DayOfWeek dayOfWeek){
    return start.datesUntil(end)
      .filter(day -> day.getDayOfWeek().equals(dayOfWeek)).collect(Collectors.toList());
  }
}
