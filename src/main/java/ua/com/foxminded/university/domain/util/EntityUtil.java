package ua.com.foxminded.university.domain.util;

import ua.com.foxminded.university.domain.entity.IEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class EntityUtil {

    private EntityUtil() {
    }

    public static List<Integer> extractIdsFromEntities(Iterable<IEntity> entities){
        return StreamSupport.stream(entities.spliterator(), false)
            .map(IEntity::getId)
            .collect(Collectors.toList());
    }
}
