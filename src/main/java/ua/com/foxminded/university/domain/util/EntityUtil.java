package ua.com.foxminded.university.domain.util;

import ua.com.foxminded.university.domain.entity.GenericEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class EntityUtil {

    private EntityUtil() {
    }

    public static List<Integer> extractIdsFromEntities(Iterable<? extends GenericEntity> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
            .map(GenericEntity::getId)
            .collect(Collectors.toList());
    }
}
