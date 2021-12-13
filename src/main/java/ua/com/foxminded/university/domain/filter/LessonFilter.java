package ua.com.foxminded.university.domain.filter;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import ua.com.foxminded.university.domain.entity.QLesson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.DATE_TIME_PATTERN;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonFilter {

    private Integer facultyId;
    private Integer departmentId;
    private Integer teacherId;
    private Integer courseId;
    private Integer roomId;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime dateFrom;

    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime dateTo;


    public Predicate getPredicate() {
        log.debug("create predicate from filter");
        List<Predicate> predicates = new ArrayList<>();

        createTeacherDepartmentFacultyPredicate(predicates);
        createCoursePredicate(predicates);
        createRoomPredicate(predicates);
        createDatePredicate(predicates);

        return ExpressionUtils.allOf(predicates);
    }


    private void createTeacherDepartmentFacultyPredicate(List<Predicate> predicates) {
        if (this.teacherId != null && this.teacherId > 0) {
            predicates.add(QLesson.lesson.teacher.id.eq(teacherId));
        } else if (this.departmentId != null && this.departmentId > 0) {
            predicates.add(QLesson.lesson.teacher.department.id.eq(departmentId));
        } else if (this.facultyId != null && this.facultyId > 0) {
            predicates.add(QLesson.lesson.teacher.department.faculty.id.eq(facultyId));
        }
    }

    private void createCoursePredicate(List<Predicate> predicates) {
        if (this.courseId != null && courseId > 0) {
            predicates.add(QLesson.lesson.course.id.eq(courseId));
        }
    }

    private void createRoomPredicate(List<Predicate> predicates) {
        if (this.roomId != null && roomId > 0) {
            predicates.add(QLesson.lesson.room.id.eq(roomId));
        }
    }

    private void createDatePredicate(List<Predicate> predicates) {
        if (dateFrom != null && dateTo != null) {
            predicates.add(QLesson.lesson.timeStart.between(dateFrom, dateTo));
        } else if (dateFrom != null) {
            predicates.add(QLesson.lesson.timeStart.goe(dateFrom));
        } else if (dateTo != null) {
            predicates.add(QLesson.lesson.timeStart.loe(dateTo));
        }
    }

}
