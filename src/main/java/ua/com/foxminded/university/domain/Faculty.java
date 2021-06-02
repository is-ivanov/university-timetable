package ua.com.foxminded.university.domain;

public class Faculty {

    private int id;
    private String name;
    private Teacher dean;

    public Faculty(String name) {
        this.name = name;
    }

    public Faculty(String name, Teacher dean) {
        this.name = name;
        this.dean = dean;
    }

    public Faculty(int id, String name, Teacher dean) {
        this.id = id;
        this.name = name;
        this.dean = dean;
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

    public Teacher getDean() {
        return dean;
    }

    public void setDean(Teacher dean) {
        this.dean = dean;
    }

}
