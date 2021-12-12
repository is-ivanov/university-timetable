//package ua.com.foxminded.university.dao.jpaimpl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Repository;
//import ua.com.foxminded.university.dao.interfaces.FacultyRepository;
//import ua.com.foxminded.university.domain.entity.Faculty;
//import ua.com.foxminded.university.exception.DaoException;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.Query;
//import javax.persistence.TypedQuery;
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@Repository
//@RequiredArgsConstructor
//@PropertySource("classpath:queries/jpql_query.properties")
//public class FacultyJpaRepository implements FacultyRepository {
//
//    public static final String FACULTY_NAME = "faculty_name";
//    public static final String MESSAGE_DELETE_FACULTY_NOT_FOUND = "Can't delete because faculty id(%d) not found";
//
//    private static final String QUERY_GET_ALL = "faculty.getAll";
//    private static final String QUERY_GET_ALL_SORTED_PAGINATED = "faculty.getAllSortedPaginated";
//    private static final String QUERY_GET_ALL_SORTED_NAME_ASC = "faculty.getAllSortedByNameAsc";
//    private static final String QUERY_DELETE_BY_ID = "faculty.deleteById";
//    private static final String QUERY_COUNT_ALL = "faculty.countAll";
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    private final Environment env;
//
//    @Override
//    public void add(Faculty faculty) {
//        log.debug("Saving {}", faculty);
//        entityManager.persist(faculty);
//        log.debug("{} saved successfully", faculty);
//    }
//
//    @Override
//    public Optional<Faculty> getById(int id) {
//        log.debug("Getting faculty by id({})", id);
//        Faculty result = entityManager.find(Faculty.class, id);
//        log.debug("Found {}", result);
//        return Optional.ofNullable(result);
//    }
//
//    @Override
//    public List<Faculty> getAll() {
//        log.debug("Getting all faculties");
//        List<Faculty> faculties = entityManager.createQuery(env.getProperty(QUERY_GET_ALL),
//            Faculty.class).getResultList();
//        log.debug("Found {} faculties", faculties.size());
//        return faculties;
//    }
//
//    @Override
//    public void update(Faculty faculty) {
//        entityManager.merge(faculty);
//        log.debug("Update {}", faculty);
//    }
//
//    @Override
//    public void delete(Faculty faculty) {
//        entityManager.remove(faculty);
//        log.debug("Delete {}", faculty);
//    }
//
//    @Override
//    public void delete(int id) {
//        int rowsDeleted = entityManager.createQuery(
//                env.getProperty(QUERY_DELETE_BY_ID))
//            .setParameter("id", id)
//            .executeUpdate();
//        if (rowsDeleted == 0) {
//            log.warn("Can't delete faculty id({})", id);
//            throw new DaoException(
//                String.format(MESSAGE_DELETE_FACULTY_NOT_FOUND, id));
//        } else {
//            log.debug("Delete faculty id({})", id);
//        }
//    }
//
//    @Override
//    public List<Faculty> getAllSortedByNameAsc() {
//        log.debug("Getting all faculties sorted by name ascending");
//        List<Faculty> faculties = entityManager.createQuery(
//                env.getProperty(QUERY_GET_ALL_SORTED_NAME_ASC), Faculty.class)
//            .getResultList();
//        log.debug("Found {} sorted faculties", faculties.size());
//        return faculties;
//    }
//
//    @Override
//    public Page<Faculty> getAllSortedPaginated(Pageable pageable) {
//        log.debug("Getting sorted page {} from list of faculties", pageable.getPageNumber());
//        Sort.Order order;
//        if (!pageable.getSort().isEmpty()) {
//            order = pageable.getSort().toList().get(0);
//        } else {
//            order = Sort.Order.by(FACULTY_NAME);
//        }
//        String queryString = String.format(env.getRequiredProperty(QUERY_GET_ALL_SORTED_PAGINATED),
//            order.getProperty(), order.getDirection().name());
//        TypedQuery<Faculty> query = entityManager.createQuery(queryString, Faculty.class);
//        List<Faculty> faculties = query
//            .setFirstResult((int) pageable.getOffset())
//            .setMaxResults(pageable.getPageSize())
//            .getResultList();
//        log.debug("Found {} faculties", faculties.size());
//        return new PageImpl<>(faculties, pageable, countAll());
//    }
//
//    @Override
//    public int countAll() {
//        log.debug("Count all faculties in database");
//        Query query = entityManager.createQuery(env.getProperty(QUERY_COUNT_ALL));
//        return ((Long) query.getSingleResult()).intValue();
//    }
//}
