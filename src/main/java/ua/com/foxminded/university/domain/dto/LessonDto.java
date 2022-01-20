package ua.com.foxminded.university.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ua.com.foxminded.university.domain.validator.LessonsTime;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

import static ua.com.foxminded.university.ui.util.ResponseUtil.DATE_TIME_PATTERN;

@Value
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class LessonDto extends AbstractDto<LessonDto> {

    private static final String TIME_START_FROM = "07:30";
    private static final String TIME_START_TO = "19:00";
    private static final String TIME_END_FROM = "09:00";
    private static final String TIME_END_TO = "20:30";

    Integer id;

    @NotNull
    int courseId;

    String courseName;

    @NotNull
    int teacherId;

    String teacherFullName;

    @NotNull
    int roomId;

    String buildingAndRoom;
    Set<StudentDto> students;

    @LessonsTime(from = TIME_START_FROM, to = TIME_START_TO)
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime timeStart;

    @LessonsTime(from = TIME_END_FROM, to = TIME_END_TO)
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime timeEnd;

//    public LessonDto(int courseId, int teacherId, int roomId,
//                     LocalDateTime timeStart, LocalDateTime timeEnd) {
//        this.courseId = courseId;
//        this.teacherId = teacherId;
//        this.roomId = roomId;
//        this.timeStart = timeStart;
//        this.timeEnd = timeEnd;
//    }
}
