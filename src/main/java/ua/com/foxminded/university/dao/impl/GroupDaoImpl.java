package ua.com.foxminded.university.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.GroupDao;
import ua.com.foxminded.university.dao.mapper.GroupMapper;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.exception.DAOException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
@PropertySource("classpath:sql_query.properties")
public class GroupDaoImpl implements GroupDao {

    private static final String QUERY_ADD = "group.add";
    private static final String QUERY_GET_ALL = "group.getAll";
    private static final String QUERY_GET_ALL_BY_FACULTY = "group.getAllByFaculty";
    private static final String QUERY_GET_BY_ID = "group.getById";
    private static final String QUERY_UPDATE = "group.update";
    private static final String QUERY_DELETE = "group.delete";
    private static final String MESSAGE_GROUP_NOT_FOUND = "Group id(%d) not found";
    private static final String MESSAGE_UPDATE_GROUP_NOT_FOUND = "Can't update because group id(%d) not found";
    private static final String MESSAGE_DELETE_GROUP_NOT_FOUND = "Can't delete because group id(%d) not found";

    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    @Override
    public void add(Group group) {
        log.debug("Adding {}", group);
        try {
            jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                group.getName(), group.isActive(), group.getFaculty().getId());
        } catch (DataAccessException e) {
            log.error("An error occurred while adding the {}", group, e);
            throw new DAOException(e.getMessage(), e);
        }
        log.info("{} added successfully", group);
    }

    @Override
    public Optional<Group> getById(int id) {
        log.debug("Getting group by id({})", id);
        Group result;
        try {
            result = jdbcTemplate.queryForObject(
                env.getRequiredProperty(QUERY_GET_BY_ID),
                new GroupMapper(), id);
        } catch (DataAccessException e) {
            log.error("Group id({}) not found", id, e);
            throw new DAOException(String.format(MESSAGE_GROUP_NOT_FOUND, id), e);
        }
        log.info("Found {}", result);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Group> getAll() {
        log.debug("Getting all groups");
        List<Group> groups = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL), new GroupMapper());
        log.info("Found {} groups", groups.size());
        return groups;
    }

    @Override
    public void update(Group group) {
        log.debug("Updating {}", group);
        int numberUpdatedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_UPDATE), group.getName(),
            group.isActive(), group.getFaculty().getId(), group.getId());
        if (numberUpdatedRows == 0) {
            log.warn("Can't update {}", group);
            throw new DAOException(String.format(MESSAGE_UPDATE_GROUP_NOT_FOUND,
                group.getId()));
        } else {
            log.info("Update {}", group);
        }
    }

    @Override
    public void delete(Group group) {
        log.debug("Deleting {}", group);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), group.getId());
        if (numberDeletedRows == 0) {
            log.warn("Can't delete {}", group);
            throw new DAOException(String.format(MESSAGE_DELETE_GROUP_NOT_FOUND,
                group.getId()));
        } else {
            log.info("Delete {}", group);
        }
    }

    @Override
    public List<Group> getAllByFacultyId(int facultyId) {
        log.debug("Getting all groups by faculty id({})", facultyId);
        List<Group> groups = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_BY_FACULTY), new GroupMapper(),
            facultyId);
        log.info("Found {} groups", groups.size());
        return groups;
    }


}