package ua.com.foxminded.university.domain;

public class Teacher extends Person {

    private Department department;

    public Teacher(String firstName, String patronymic, String lastName,
            Department department) {
        super(firstName, patronymic, lastName);
        this.department = department;
    }

    public Teacher(int id, String firstName, String patronymic, String lastName,
            Department department) {
        super(id, firstName, patronymic, lastName);
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

}
