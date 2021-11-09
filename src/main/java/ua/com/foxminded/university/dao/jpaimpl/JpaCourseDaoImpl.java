package ua.com.foxminded.university.dao.jpaimpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaCourseDaoImpl implements CourseDao {

    private static final String COURSE_NAME = "course_name";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Course course) {
        entityManager.persist(course);
    }

    @Override
    public Optional<Course> getById(int id) {
        Course course = entityManager.find(Course.class, id);
        return Optional.ofNullable(course);
    }

    @Override
    public List<Course> getAll() {
        return entityManager.createQuery("SELECT c FROM Course c",
            Course.class).getResultList();
    }

    @Override
    public void update(Course course) {
        entityManager.merge(course);
    }

    @Override
    public void delete(Course course) {
        entityManager.remove(course);
    }

    @Override
    public void delete(int id) {
        Course course = entityManager.find(Course.class, id);
        entityManager.remove(course);
    }

    @Override
    public int countAll() {
        Query query = entityManager.createQuery("SELECT count(c.id) FROM Course c");
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    public Page<Course> getAllSortedPaginated(Pageable pageable) {
        Sort.Order order;
        if (!pageable.getSort().isEmpty()) {
            order = pageable.getSort().toList().get(0);
        } else {
            order = Sort.Order.by(COURSE_NAME);
        }
        String queryString = String.format("SELECT c FROM Course c ORDER BY %s %s",
            order.getProperty(), order.getDirection().name());
        TypedQuery<Course> query = entityManager.createQuery(queryString, Course.class);
        List<Course> courses = query
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();
        return new PageImpl<>(courses, pageable, countAll());
    }
}
