package ua.com.foxminded.university.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ua.com.foxminded.university.domain.validator.LessonsTime;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

import static ua.com.foxminded.university.ui.Util.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDto {

    private Integer id;

    @NotNull
    private int courseId;

    private String courseName;

    @NotNull
    private int teacherId;

    private String teacherFullName;

    @NotNull
    private int roomId;

    private String buildingAndRoom;
    private Set<StudentDto> students;

    @LessonsTime(from = "07:30", to = "19:00")
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timeStart;

    @LessonsTime(from = "09:00", to = "20:30")
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timeEnd;

    public LessonDto(int courseId, int teacherId, int roomId,
                     LocalDateTime timeStart, LocalDateTime timeEnd) {
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.roomId = roomId;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }
}
