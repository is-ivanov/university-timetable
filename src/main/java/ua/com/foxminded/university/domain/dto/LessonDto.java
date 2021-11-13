package ua.com.foxminded.university.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static ua.com.foxminded.university.ui.Util.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDto {


    private int id;
    private int courseId;
    private String courseName;
    private int teacherId;
    private String teacherFullName;
    private int roomId;
    private String buildingAndRoom;
    private Set<StudentDto> students = new HashSet<>();

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timeStart;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timeEnd;

}
