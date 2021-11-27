package ua.com.foxminded.university.dao.jpaimpl;

import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/teacher-test-data.sql")
class JpaTeacherDaoImplTest extends IntegrationTestBase {

}