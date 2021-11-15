package ua.com.foxminded.university.dao.jpaimpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.DaoException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaFacultyDaoImpl implements FacultyDao {

    public static final String MESSAGE_DELETE_FACULTY_NOT_FOUND = "Can't delete because faculty id(%d) not found";
    public static final String FACULTY_NAME = "faculty_name";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Faculty faculty) {
        entityManager.persist(faculty);
    }

    @Override
    public Optional<Faculty> getById(int id) {
        Faculty faculty = entityManager.find(Faculty.class, id);
        return Optional.ofNullable(faculty);
    }

    @Override
    public List<Faculty> getAll() {
        return entityManager.createQuery("SELECT f FROM Faculty f",
            Faculty.class).getResultList();
    }

    @Override
    public void update(Faculty faculty) {
        entityManager.merge(faculty);
    }

    @Override
    public void delete(Faculty faculty) {
        entityManager.remove(faculty);
    }

    @Override
    public void delete(int id) {
        int rowsDeleted = entityManager.createQuery("DELETE FROM Faculty f WHERE f.id = :id")
            .setParameter("id", id)
            .executeUpdate();
        if (rowsDeleted == 0) {
            throw new DaoException(String.format(MESSAGE_DELETE_FACULTY_NOT_FOUND,
                id));
        }
    }

//    public void delete(int id) {
//        Faculty faculty = entityManager.find(Faculty.class, id);
//        entityManager.remove(faculty);
//    }


    @Override
    public List<Faculty> getAllSortedByNameAsc() {
        return entityManager.createQuery(
                "SELECT f FROM Faculty f ORDER BY f.name ASC", Faculty.class)
            .getResultList();
    }

    @Override
    public Page<Faculty> getAllSortedPaginated(Pageable pageable) {
        Sort.Order order;
        if (!pageable.getSort().isEmpty()) {
            order = pageable.getSort().toList().get(0);
        } else {
            order = Sort.Order.by(FACULTY_NAME);
        }
        String queryString = String.format("SELECT f FROM Faculty f ORDER BY %s %s",
            order.getProperty(), order.getDirection().name());
        TypedQuery<Faculty> query = entityManager.createQuery(queryString, Faculty.class);
        List<Faculty> faculties = query
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();
        return new PageImpl<>(faculties, pageable, countAll());
    }

    @Override
    public int countAll() {
        Query query = entityManager.createQuery("SELECT count(f.id) FROM Faculty f");
        return ((Long) query.getSingleResult()).intValue();
    }
}
