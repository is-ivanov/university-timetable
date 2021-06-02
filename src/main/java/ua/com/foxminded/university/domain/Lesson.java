package ua.com.foxminded.university.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Lesson {

    private int id;
    private Teacher teacher;
    private List<Student> students;
    private Course course;
    private Room room;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;

    public Lesson(Teacher teacher, List<Student> students, Course course,
            Room room, LocalDateTime timeStart, LocalDateTime timeEnd) {
        this.teacher = teacher;
        this.students = students;
        this.course = course;
        this.room = room;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public Lesson(int id, Teacher teacher, List<Student> students,
            Course course, Room room, LocalDateTime timeStart,
            LocalDateTime timeEnd) {
        this.id = id;
        this.teacher = teacher;
        this.students = students;
        this.course = course;
        this.room = room;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalDateTime timeEnd) {
        this.timeEnd = timeEnd;
    }


}
