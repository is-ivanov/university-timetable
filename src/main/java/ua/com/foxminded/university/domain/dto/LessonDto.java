package ua.com.foxminded.university.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDto {

    private Integer id;
    private String courseName;
    private String teacherFullName;
    private String buildingAndRoom;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;

}
