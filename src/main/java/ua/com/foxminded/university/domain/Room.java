package ua.com.foxminded.university.domain;

public class Room {

    private int id;
    private String building;
    private String number;

    public Room(String building, String number) {
        this.building = building;
        this.number = number;
    }

    public Room(int id, String building, String number) {
        this.id = id;
        this.building = building;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
