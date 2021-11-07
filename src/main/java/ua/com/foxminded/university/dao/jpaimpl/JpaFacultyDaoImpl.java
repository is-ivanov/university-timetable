package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.domain.entity.Faculty;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaFacultyDaoImpl implements FacultyDao {

    public static final String FACULTY_NAME = "faculty_name";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Faculty faculty) {

    }

    @Override
    public Optional<Faculty> getById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Faculty> getAll() {
        return entityManager.createQuery("SELECT f FROM Faculty f", Faculty.class).getResultList();
    }

    @Override
    public void update(Faculty faculty) {

    }

    @Override
    public void delete(Faculty faculty) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Faculty> getAllSortedByNameAsc() {
        return null;
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
        List<Faculty> faculties = query.setFirstResult((int) pageable.getOffset())
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
