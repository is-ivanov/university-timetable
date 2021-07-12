package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.interfaces.GroupDao;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.mapper.GroupMapper;
import ua.com.foxminded.university.exception.DAOException;

@Repository
@PropertySource("classpath:sql_query.properties")
public class GroupDaoImpl implements GroupDao {

    private static final String QUERY_ADD = "group.add";
    private static final String QUERY_GET_ALL = "group.getAll";
    private static final String QUERY_GET_BY_ID = "group.getById";
    private static final String QUERY_UPDATE = "group.update";
    private static final String QUERY_DELETE = "group.delete";
    private static final String MESSAGE_GROUP_NOT_FOUND = "Group not found: ";

    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    @Autowired
    public GroupDaoImpl(JdbcTemplate jdbcTemplate, Environment env) {
        this.jdbcTemplate = jdbcTemplate;
        this.env = env;
    }

    @Override
    public void add(Group group) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD), group.getName(),
                group.isActive(), group.getFaculty().getId());
    }

    @Override
    public Optional<Group> getById(int id) {
        Group result;
        try {
            result = jdbcTemplate.queryForObject(
                    env.getRequiredProperty(QUERY_GET_BY_ID),
                    new GroupMapper(), id);
        } catch (DataAccessException e) {
            throw new DAOException(MESSAGE_GROUP_NOT_FOUND + id, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Group> getAll() {
        return jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL),
                new GroupMapper());
    }

    @Override
    public void update(Group group) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_UPDATE),
                group.getName(), group.isActive(),
                group.getFaculty().getId(), group.getId());
    }

    @Override
    public void delete(Group group) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_DELETE),
                group.getId());
    }


}
