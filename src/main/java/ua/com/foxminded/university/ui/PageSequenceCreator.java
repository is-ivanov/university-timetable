package ua.com.foxminded.university.ui;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class PageSequenceCreator {

    public List<Integer> createPageSequence(Page page){
        int totalPages = page.getTotalPages();
        int pageNumber = page.getNumber() + 1;

        List<Integer> result = new ArrayList<>();
        if (pageNumber > 3) {
            result.addAll()
            int[] head = {1, -1};
        } else {
            int[] head = {1, 2, 3};
        }
        if (pageNumber > 3 && pageNumber < (totalPages - 1)) {
            int bodyBefore = pageNumber - 1;
        } else {
            int bodyBefore =
        }
    }
}
