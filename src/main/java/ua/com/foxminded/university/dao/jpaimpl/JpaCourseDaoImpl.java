package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.exception.DaoException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@PropertySource("/queries/jpql_query.properties")
public class JpaCourseDaoImpl implements CourseDao {

    private static final String COURSE_NAME = "course_name";
    private static final String MESSAGE_DELETE_COURSE_NOT_FOUND = "Can't delete because course id(%s) not found";
    private static final String QUERY_GET_ALL = "Course.getAll";
    private static final String QUERY_GET_ALL_SORTED_PAGINATED = "Course.getAllSortedPaginated";
    private static final String QUERY_DELETE_BY_ID = "Course.deleteById";
    private static final String QUERY_COUNT_ALL = "Course.countAll";

    @PersistenceContext
    private EntityManager entityManager;

    private final Environment env;

    @Override
    public void add(Course course) {
        log.debug("Saving {}", course);
        entityManager.persist(course);
        log.info("{} saved successfully", course);
    }

    @Override
    public Optional<Course> getById(int id) {
        log.debug("Getting course by id({})", id);
        Course course = entityManager.find(Course.class, id);
        log.info("Found {}", course);
        return Optional.ofNullable(course);
    }

    @Override
    public List<Course> getAll() {
        log.debug("Getting all courses");
        List<Course> courses = entityManager.createQuery(env.getProperty(QUERY_GET_ALL),
            Course.class).getResultList();
        log.info("Found {} courses", courses.size());
        return courses;
    }

    @Override
    public void update(Course course) {
        entityManager.merge(course);
        log.info("Update {}", course);
    }

    @Override
    public void delete(Course course) {
        entityManager.remove(course);
        log.info("Delete {}", course);
    }

    @Override
    public void delete(int id) {
        int rowsDeleted = entityManager.createQuery(env.getProperty(QUERY_DELETE_BY_ID))
            .setParameter("id", id)
            .executeUpdate();
        if (rowsDeleted == 0) {
            log.warn("Can't delete course id({})", id);
            throw new DaoException(
                String.format(MESSAGE_DELETE_COURSE_NOT_FOUND, id));
        } else {
            log.info("Delete course id({})", id);
        }
    }

    @Override
    public int countAll() {
        log.debug("Count all courses in database");
        Query query = entityManager.createQuery(env.getProperty(QUERY_COUNT_ALL));
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    public Page<Course> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of courses", pageable.getPageNumber());
        Sort.Order order;
        if (!pageable.getSort().isEmpty()) {
            order = pageable.getSort().toList().get(0);
        } else {
            order = Sort.Order.by(COURSE_NAME);
        }
        String queryString = String.format(env.getRequiredProperty(QUERY_GET_ALL_SORTED_PAGINATED),
            order.getProperty(), order.getDirection().name());
        TypedQuery<Course> query = entityManager.createQuery(queryString, Course.class);
        List<Course> courses = query
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();
        log.info("Found {} courses", courses.size());
        return new PageImpl<>(courses, pageable, countAll());
    }
}
