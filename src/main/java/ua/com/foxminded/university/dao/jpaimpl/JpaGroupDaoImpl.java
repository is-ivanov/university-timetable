package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.GroupDao;
import ua.com.foxminded.university.domain.entity.Group;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@PropertySource("classpath:jpql_query.properties")
public class JpaGroupDaoImpl implements GroupDao {

    private static final String QUERY_GET_FREE_GROUPS = "group.getFreeGroupsOnLessonTime";
    private static final String QUERY_GET_FREE_GROUPS_BY_FACULTY = "group.getFreeGroupsByFacultyOnLessonTime";

    private final Environment env;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Group group) {
        entityManager.persist(group);
    }

    @Override
    public Optional<Group> getById(int id) {
        Group group = entityManager.find(Group.class, id);
        return Optional.ofNullable(group);
    }

    @Override
    public List<Group> getAll() {
        return entityManager.createQuery("SELECT g FROM Group g",
            Group.class).getResultList();
    }

    @Override
    public void update(Group group) {
        entityManager.merge(group);
    }

    @Override
    public void delete(Group group) {
        entityManager.remove(group);
    }

    @Override //TODO
    public void delete(int id) {
        entityManager.createQuery("DELETE FROM Group g WHERE g.id = :id")
            .setParameter("id", id)
            .executeUpdate();
    }

    @Override
    public List<Group> getAllByFacultyId(int facultyId) {
        TypedQuery<Group> query = entityManager.createQuery(
            "SELECT g FROM Group g WHERE g.faculty.id = :facultyId",
            Group.class);
        query.setParameter("facultyId", facultyId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Group> getFreeGroupsOnLessonTime(LocalDateTime startTime,
                                                 LocalDateTime endTime) {
        return entityManager.createNativeQuery(
                env.getProperty(QUERY_GET_FREE_GROUPS), Group.class)
            .setParameter("start_time", startTime)
            .setParameter("end_time", endTime)
            .getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Group> getFreeGroupsByFacultyOnLessonTime(int facultyId,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime) {
        return entityManager.createNativeQuery(
                env.getProperty(QUERY_GET_FREE_GROUPS_BY_FACULTY), Group.class)
            .setParameter("facultyId", facultyId)
            .setParameter("start_time", startTime)
            .setParameter("end_time", endTime)
            .getResultList();
    }

    @Override
    public List<Group> getActiveGroups() {
        return entityManager.createQuery(
                "SELECT g FROM Group g WHERE g.active = TRUE", Group.class)
            .getResultList();
    }
}
