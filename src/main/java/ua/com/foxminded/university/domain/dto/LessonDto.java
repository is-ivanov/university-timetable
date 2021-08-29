package ua.com.foxminded.university.domain.dto;

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

    private Integer id;
    private int courseId;
    private String courseName;
    private int teacherId;
    private String teacherFullName;
    private int roomId;
    private String buildingAndRoom;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timeEnd;

}
