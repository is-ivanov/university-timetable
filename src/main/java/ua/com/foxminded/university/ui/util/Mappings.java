package ua.com.foxminded.university.ui.util;

public final class Mappings {

    public static final String API = "/api";
    public static final String FACULTIES = "/faculties";
    public static final String GROUPS = "/groups";
    public static final String ROOMS = "/rooms";
    public static final String DEPARTMENTS = "/departments";
    public static final String TEACHERS = "/teachers";
    public static final String FREE = "/free";
    public static final String ID = "/{id}";
    public static final String API_FACULTIES = API + FACULTIES;
    public static final String API_FACULTY = API + FACULTIES + ID;
    public static final String API_GROUPS = API + GROUPS;
    public static final String API_ROOMS = API + ROOMS;
    public static final String ID_GROUPS = ID + GROUPS;
    public static final String ID_DEPARTMENTS = ID + DEPARTMENTS;
    public static final String ID_TEACHERS = ID + TEACHERS;
    public static final String ID_GROUPS_FREE = ID + GROUPS + FREE;


    private Mappings() {
    }
}
