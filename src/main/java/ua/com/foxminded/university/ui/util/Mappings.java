package ua.com.foxminded.university.ui.util;

public final class Mappings {

    public static final String API = "/api";
    public static final String FACULTIES = "/faculties";
    public static final String ID = "/{id}";
    public static final String API_FACULTIES = API + FACULTIES;
    public static final String API_FACULTY = API + FACULTIES + ID;


    private Mappings() {
    }
}
