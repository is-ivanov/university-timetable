package ua.com.foxminded.university.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private List<StudentDto> students;

}
