package ua.com.foxminded.university.exception;

public class MyPageNotFoundException extends RuntimeException {

    public MyPageNotFoundException(int page, int maxPages, int size) {
        super(String.format("Page number %d not found. Maximum %d pages with size %d",
            page, maxPages, size));
    }
}
