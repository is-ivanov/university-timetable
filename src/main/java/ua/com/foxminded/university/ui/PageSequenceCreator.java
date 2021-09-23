package ua.com.foxminded.university.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class PageSequenceCreator {

    public List<Integer> createPageSequence(int totalPages, int currentPage) {
        log.debug("Creating page sequence for pagination view");
        List<Integer> result = new ArrayList<>();
        if (totalPages < 6) {
            result = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
        } else {
            calculateHead(currentPage, result);
            calculateBeforeBody(totalPages, currentPage, result);
            calculateBody(totalPages, currentPage, result);
            calculateAfterBody(totalPages, currentPage, result);
            calculateTail(totalPages, currentPage, result);
        }
        log.debug("Sequence {} was created", result);
        return result;
    }

    private void calculateHead(int currentPage, List<Integer> result) {
        if (currentPage > 3) {
            result.addAll(Arrays.asList(1, -1));
        } else {
            result.addAll(Arrays.asList(1, 2, 3));
        }
    }

    private void calculateBeforeBody(int totalPages, int currentPage,
                                     List<Integer> result) {
        if (currentPage > 3 && currentPage < (totalPages - 1)) {
            result.add(currentPage - 1);
        }
    }

    private void calculateBody(int totalPages, int currentPage,
                               List<Integer> result) {
        if (currentPage > 3 && currentPage < (totalPages - 2)) {
            result.add(currentPage);
        }
    }

    private void calculateAfterBody(int totalPages, int currentPage,
                                    List<Integer> result) {
        if (currentPage > 2 && currentPage < (totalPages - 2)) {
            result.add(currentPage + 1);
        }
    }

    private void calculateTail(int totalPages, int currentPage,
                               List<Integer> result) {
        if (currentPage < (totalPages - 2)) {
            result.addAll(Arrays.asList(-1, totalPages));
        } else {
            result.addAll(Arrays.asList(totalPages - 2, totalPages - 1, totalPages));
        }
    }

}
