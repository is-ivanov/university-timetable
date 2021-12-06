package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.DepartmentRepository;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.exception.DaoException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@PropertySource("classpath:queries/jpql_query.properties")
public class DepartmentRepositoryJpa implements DepartmentRepository {

    private static final String QUERY_GET_ALL = "department.getAll";
    private static final String QUERY_GET_ALL_BY_FACULTY = "department.getAllByFacultyId";
    private static final String QUERY_DELETE_BY_ID = "department.deleteById";
    private static final String MESSAGE_DELETE_DEPARTMENT_NOT_FOUND = "Can't delete because department id(%s) not found";

    private final Environment env;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Department department) {
        log.debug("Saving {}", department);
        entityManager.persist(department);
        log.debug("{} saved successfully", department);
    }

    @Override
    public Optional<Department> getById(int id) {
        log.debug("Getting department by id({})", id);
        Department department = entityManager.find(Department.class, id);
        log.debug("Found {}", department);
        return Optional.ofNullable(department);
    }

    @Override
    public List<Department> getAll() {
        log.debug("Getting all departments");
        List<Department> departments = entityManager
            .createQuery(env.getProperty(QUERY_GET_ALL),
                Department.class).getResultList();
        log.debug("Found {} departments", departments.size());
        return departments;
    }

    @Override
    public void update(Department department) {
        entityManager.merge(department);
        log.debug("Update {}", department);
    }

    @Override
    public void delete(Department department) {
        entityManager.remove(department);
        log.debug("Delete {}", department);
    }

    @Override
    public void delete(int id) {
        int rowsDeleted = entityManager
            .createQuery(env.getProperty(QUERY_DELETE_BY_ID))
            .setParameter("id", id)
            .executeUpdate();
        if (rowsDeleted == 0) {
            log.warn("Can't delete department id({})", id);
            throw new DaoException(String
                .format(MESSAGE_DELETE_DEPARTMENT_NOT_FOUND, id));
        } else {
            log.debug("Delete department id({})", id);
        }
    }

    @Override
    public List<Department> getAllByFacultyId(int facultyId) {
        TypedQuery<Department> query = entityManager.createQuery(
            env.getProperty(QUERY_GET_ALL_BY_FACULTY),
            Department.class);
        query.setParameter("facultyId", facultyId);
        List<Department> departments = query.getResultList();
        log.debug("Found {} departments", departments.size());
        return departments;
    }
}
