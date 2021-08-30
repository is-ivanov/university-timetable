package ua.com.foxminded.university.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDto {

    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";

    private Integer id;
    private int courseId;
    private String courseName;
    private int teacherId;
    private String teacherFullName;
    private int roomId;
    private String buildingAndRoom;

    @DateTimeFormat(pattern = FORMAT_DATE_TIME)
    @JsonFormat(pattern = FORMAT_DATE_TIME)
    private LocalDateTime timeStart;

    @DateTimeFormat(pattern = FORMAT_DATE_TIME)
    @JsonFormat(pattern = FORMAT_DATE_TIME)
    private LocalDateTime timeEnd;

}
