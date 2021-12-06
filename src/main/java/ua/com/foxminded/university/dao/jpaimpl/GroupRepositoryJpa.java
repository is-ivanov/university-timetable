package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.GroupRepository;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.exception.DaoException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@PropertySource("classpath:queries/jpql_query.properties")
public class GroupRepositoryJpa implements GroupRepository {

    private static final String QUERY_GET_ALL = "group.getAll";
    private static final String QUERY_GET_ALL_ACTIVE = "group.getAllActive";
    private static final String QUERY_GET_ALL_BY_FACULTY = "group.getAllByFaculty";
    private static final String QUERY_DELETE_BY_ID = "group.deleteById";
    private static final String QUERY_GET_FREE_GROUPS = "group.getFreeGroupsOnLessonTime";
    private static final String QUERY_GET_FREE_GROUPS_BY_FACULTY = "group.getFreeGroupsByFacultyOnLessonTime";
    private static final String LOG_FOUND_GROUPS = "Found {} groups";
    private static final String MESSAGE_DELETE_GROUP_NOT_FOUND = "Can't delete because group id(%d) not found";

    private final Environment env;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Group group) {
        log.debug("Saving {}", group);
        entityManager.persist(group);
        log.debug("{} saved successfully", group);
    }

    @Override
    public Optional<Group> getById(int id) {
        log.debug("Getting group by id({})", id);
        Group group = entityManager.find(Group.class, id);
        log.debug("Found {}", group);
        return Optional.ofNullable(group);
    }

    @Override
    public List<Group> getAll() {
        log.debug("Getting all groups");
        List<Group> groups = entityManager
            .createQuery(env.getProperty(QUERY_GET_ALL),
                Group.class).getResultList();
        log.debug(LOG_FOUND_GROUPS, groups.size());
        return groups;
    }

    @Override
    public void update(Group group) {
        entityManager.merge(group);
        log.debug("Update {}", group);
    }

    @Override
    public void delete(Group group) {
        entityManager.remove(group);
        log.debug("Delete {}", group);
    }

    @Override
    public void delete(int id) {
        int rowsDeleted = entityManager
            .createQuery(env.getProperty(QUERY_DELETE_BY_ID))
            .setParameter("id", id)
            .executeUpdate();
        if (rowsDeleted == 0) {
            log.warn("Can't delete group id({})", id);
            throw new DaoException(
                String.format(MESSAGE_DELETE_GROUP_NOT_FOUND, id));
        } else {
            log.debug("Delete group id({})", id);
        }
    }

    @Override
    public List<Group> getAllByFacultyId(int facultyId) {
        TypedQuery<Group> query = entityManager.createQuery(
            env.getProperty(QUERY_GET_ALL_BY_FACULTY),
            Group.class);
        query.setParameter("facultyId", facultyId);
        List<Group> groups = query.getResultList();
        log.debug(LOG_FOUND_GROUPS, groups.size());
        return groups;
    }

    @Override
    public List<Group> getFreeGroupsOnLessonTime(LocalDateTime startTime,
                                                 LocalDateTime endTime) {
        log.debug("Getting groups free from {} to {}", startTime, endTime);
        List<Group> groups = entityManager.createQuery(
                env.getProperty(QUERY_GET_FREE_GROUPS), Group.class)
            .setParameter("time_start", startTime)
            .setParameter("time_end", endTime)
            .getResultList();
        log.debug(LOG_FOUND_GROUPS, groups.size());
        return groups;
    }

    @Override
    public List<Group> getFreeGroupsByFacultyOnLessonTime(int facultyId,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime) {
        log.debug("Getting active groups from faculty id({}) free from {} to {}",
            facultyId, startTime, endTime);
        List<Group> freeGroups = entityManager.createQuery(
                env.getProperty(QUERY_GET_FREE_GROUPS_BY_FACULTY), Group.class)
            .setParameter("facultyId", facultyId)
            .setParameter("time_start", startTime)
            .setParameter("time_end", endTime)
            .getResultList();
        log.debug(LOG_FOUND_GROUPS, freeGroups.size());
        return freeGroups;
    }

    @Override
    public List<Group> getActiveGroups() {
        log.debug("Getting all active groups");
        List<Group> activeGroups = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_ACTIVE), Group.class)
            .getResultList();
        log.debug(LOG_FOUND_GROUPS, activeGroups.size());
        return activeGroups;
    }
}
