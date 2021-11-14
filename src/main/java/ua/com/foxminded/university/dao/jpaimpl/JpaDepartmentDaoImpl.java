package ua.com.foxminded.university.dao.jpaimpl;

import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.DepartmentDao;
import ua.com.foxminded.university.domain.entity.Department;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaDepartmentDaoImpl implements DepartmentDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Department department) {
        entityManager.persist(department);
    }

    @Override
    public Optional<Department> getById(int id) {
        Department department = entityManager.find(Department.class, id);
        return Optional.ofNullable(department);
    }

    @Override
    public List<Department> getAll() {
        return entityManager.createQuery("SELECT d FROM Department d",
            Department.class).getResultList();
    }

    @Override
    public void update(Department department) {
        entityManager.merge(department);
    }

    @Override
    public void delete(Department department) {
        entityManager.remove(department);
    }

    @Override
    public void delete(int id) {
        Department department = entityManager.find(Department.class, id);
        entityManager.remove(department);
    }

    @Override
    public List<Department> getAllByFacultyId(int facultyId) {
        TypedQuery<Department> query = entityManager
            .createQuery("SELECT d FROM Department d, Faculty f WHERE f.id = :facultyId", Department.class);
        query.setParameter("facultyId", facultyId);
         return query.getResultList();
    }
}
