package ua.com.foxminded.university.domain;

public class Group {

    private int id;
    private String name;
    private Faculty faculty;

    public Group(String name, Faculty faculty) {
        this.name = name;
        this.faculty = faculty;
    }

    public Group(int id, String name, Faculty faculty) {
        this.id = id;
        this.name = name;
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

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

}
