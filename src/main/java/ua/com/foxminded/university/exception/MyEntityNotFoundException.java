package ua.com.foxminded.university.exception;


public class MyEntityNotFoundException extends RuntimeException {

    public MyEntityNotFoundException(String resourceName, String fieldName,
                                     Object fieldValue) {
        super(String.format("%s with %s(%s) not found",
            resourceName, fieldName, fieldValue));
    }
}
