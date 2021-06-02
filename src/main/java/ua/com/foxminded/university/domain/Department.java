package ua.com.foxminded.university.domain;

public class Department {

    private int id;
    private String name;
    private Teacher head;
    private Faculty faculty;

    public Department(String name, Teacher head, Faculty faculty) {
        this.name = name;
        this.head = head;
        this.faculty = faculty;
    }

    public Department(int id, String name, Teacher head, Faculty faculty) {
        this.id = id;
        this.name = name;
        this.head = head;
        this.faculty = faculty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Teacher getHead() {
        return head;
    }

    public void setHead(Teacher head) {
        this.head = head;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

}
