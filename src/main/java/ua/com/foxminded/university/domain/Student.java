package ua.com.foxminded.university.domain;

public class Student extends Person {

    private Group group;

    public Student(String firstName, String patronymic, String lastName,
            Group group) {
        super(firstName, patronymic, lastName);
        this.group = group;
    }

    public Student(int id, String firstName, String patronymic, String lastName,
            Group group) {
        super(id, firstName, patronymic, lastName);
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

}
