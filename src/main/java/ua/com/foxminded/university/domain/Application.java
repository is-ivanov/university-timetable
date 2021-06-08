package ua.com.foxminded.university.domain;

import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ua.com.foxminded.university.dao.impl.CourseDaoImpl;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.DbConfig;

public class Application {

    public static void main(String[] args) throws DAOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                DbConfig.class);

        CourseDaoImpl dao = context.getBean(CourseDaoImpl.class);

        System.out.println("Create new course");
        Course newCourse = new Course();
        newCourse.setName("newCourse");
        dao.add(newCourse);

        System.out.println("get biology by id 3");
        System.out.println(dao.getById(11));

        System.out.println();
        System.out.println("update name math to mathematics");
        Course math = new Course();
        math.setId(1);
        math.setName("mathematics");
        dao.update(math);
        System.out.println();

        System.out.println("delete course with id 11");
        Course delCourse = new Course();
        delCourse.setId(11);
        dao.delete(delCourse);

        System.out.println("All Courses");
        List<Course> courses = dao.getAll();

        courses.forEach(System.out::println);

        context.close();
    }

}
