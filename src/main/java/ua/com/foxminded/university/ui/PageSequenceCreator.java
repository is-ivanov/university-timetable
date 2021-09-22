package ua.com.foxminded.university.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.entity.Faculty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class PageSequenceCreator {

    public List<Integer> createPageSequence(Page<Faculty> page) {
        int totalPages = page.getTotalPages();
        int pageNumber = page.getNumber() + 1;

        List<Integer> result = new ArrayList<>();
        if (totalPages < 6) {
            result = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
        } else {
            calculateHead(pageNumber, result);
            calculateBeforeBody(totalPages, pageNumber, result);
            calculateBody(totalPages, pageNumber, result);
            calculateAfterBody(totalPages, pageNumber, result);
            calculateTail(totalPages, pageNumber, result);
        }
        return result;
    }

    private void calculateHead(int pageNumber, List<Integer> result) {
        if (pageNumber > 3) {
            result.addAll(Arrays.asList(1, -1));
        } else {
            result.addAll(Arrays.asList(1, 2, 3));
        }
    }

    private void calculateBeforeBody(int totalPages, int pageNumber,
                                     List<Integer> result) {
        if (pageNumber > 3 && pageNumber < (totalPages - 1)) {
            result.add(pageNumber - 1);
        }
    }

    private void calculateBody(int totalPages, int pageNumber,
                               List<Integer> result) {
        if (pageNumber > 3 && pageNumber < (totalPages - 2)) {
            result.add(pageNumber);
        }
    }

    private void calculateAfterBody(int totalPages, int pageNumber,
                                    List<Integer> result) {
        if (pageNumber > 2 && pageNumber < (totalPages - 2)) {
            result.add(pageNumber + 1);
        }
    }

    private void calculateTail(int totalPages, int pageNumber,
                               List<Integer> result) {
        if (pageNumber < (totalPages - 2)) {
            result.addAll(Arrays.asList(-1, totalPages));
        } else {
            result.addAll(Arrays.asList(totalPages - 2, totalPages - 1, totalPages));
        }
    }

}
