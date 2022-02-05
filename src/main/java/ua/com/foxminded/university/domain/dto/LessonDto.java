package ua.com.foxminded.university.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.*;
import lombok.experimental.NonFinal;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.server.core.Relation;
import ua.com.foxminded.university.domain.validator.LessonsTime;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static ua.com.foxminded.university.ui.util.ResponseUtil.DATE_TIME_PATTERN;

@Value
@NonFinal
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Relation(itemRelation = "lesson", collectionRelation = "lessons")
public class LessonDto extends AbstractDto<LessonDto> {

    private static final String TIME_START_FROM = "07:30";
    private static final String TIME_START_TO = "19:00";
    private static final String TIME_END_FROM = "09:00";
    private static final String TIME_END_TO = "20:30";

    Integer id;

    @NotNull
    Integer courseId;

    String courseName;

    @NotNull
    Integer teacherId;

    String teacherFullName;

    @NotNull
    Integer roomId;

    String buildingAndRoom;
    Set<StudentDto> students;

    @NotNull
    @LessonsTime(from = TIME_START_FROM, to = TIME_START_TO)
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime timeStart;

    @NotNull
    @LessonsTime(from = TIME_END_FROM, to = TIME_END_TO)
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime timeEnd;

}
