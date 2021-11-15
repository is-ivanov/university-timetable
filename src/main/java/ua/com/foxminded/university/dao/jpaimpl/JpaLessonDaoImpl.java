package ua.com.foxminded.university.dao.jpaimpl;

import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.filter.LessonFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaLessonDaoImpl implements LessonDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Lesson lesson) {
        entityManager.persist(lesson);
    }

    @Override
    public Optional<Lesson> getById(int id) {
        Lesson lesson = entityManager.find(Lesson.class, id);
        return Optional.ofNullable(lesson);
    }

    @Override
    public List<Lesson> getAll() {
        return entityManager.createQuery("SELECT l FROM Lesson l",
            Lesson.class).getResultList();
    }

    @Override
    public void update(Lesson lesson) {
        entityManager.merge(lesson);
    }

    @Override
    public void delete(Lesson lesson) {
        entityManager.remove(lesson);
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void addStudentToLesson(int lessonId, int studentId) {

    }

    @Override
    public void deleteAllStudentsFromLesson(int lessonId) {
        // First variant
        Query query = entityManager.createNativeQuery(
            "DELETE FROM university.public.students_lessons WHERE lesson_id = :lessonId");
        query.setParameter("lessonId", lessonId);
        int isSuccessful = query.executeUpdate();

        //Second variant
        Lesson lesson = entityManager.find(Lesson.class, lessonId);
        lesson.getStudents().clear();
    }

    @Override
    public List<Lesson> getAllForTeacher(int teacherId) {
        return null;
    }

    @Override
    public List<Lesson> getAllForRoom(int roomId) {
        return null;
    }

    @Override
    public List<Lesson> getAllForStudent(int studentId) {
        return null;
    }

    @Override
    public void removeStudentFromLesson(int lessonId, int studentId) {

    }

    @Override
    public List<Lesson> getAllWithFilter(LessonFilter filter) {
        return null;
    }

    @Override
    public List<Lesson> getAllForStudentForTimePeriod(int studentId, LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }

    @Override
    public List<Lesson> getAllForTeacherForTimePeriod(int studentId, LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }

    @Override
    public List<Lesson> getAllForRoomForTimePeriod(int teacherId, LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }
}
