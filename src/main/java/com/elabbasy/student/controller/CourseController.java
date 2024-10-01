package com.elabbasy.student.controller;

import com.elabbasy.student.model.dto.CourseDto;
import com.elabbasy.student.service.CourseService;
import com.itextpdf.text.DocumentException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/course")
@RestController
@RequiredArgsConstructor
public class CourseController {

  private final CourseService courseService;

  @PostMapping("/register")
  public ResponseEntity<CourseDto> register(@RequestBody @Valid CourseDto courseDto) {
    return ResponseEntity.ok(courseService.registerCourse(courseDto));
  }

  @DeleteMapping("/cancel/{id}")
  public ResponseEntity cancelCourse(@PathVariable UUID id) {
    courseService.cancelCourse(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/list")
  public ResponseEntity<List<CourseDto>> list() {
    return ResponseEntity.ok(courseService.list());
  }

  @GetMapping("/course-schedule-pdf/{id}")
  public ResponseEntity<byte[]> getCourseSchedulePdf(@PathVariable UUID id) throws DocumentException, IOException {
    ByteArrayOutputStream pdfStream = courseService.scheduleCourseAsPdf(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=query_results.pdf");
    headers.setContentLength(pdfStream.size());
    return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
  }
}
