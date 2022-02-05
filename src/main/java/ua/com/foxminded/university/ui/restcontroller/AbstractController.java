package ua.com.foxminded.university.ui.restcontroller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import ua.com.foxminded.university.domain.dto.AbstractDto;
import ua.com.foxminded.university.domain.entity.IEntity;
import ua.com.foxminded.university.domain.mapper.DtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.Service;
import ua.com.foxminded.university.exception.MyPageNotFoundException;
import ua.com.foxminded.university.ui.util.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

public abstract class AbstractController<T extends AbstractDto<T>, D extends IEntity> {

    protected CollectionModel<T> getAllInternal() {
        List<D> entities = getService().findAll();
        return getAssembler().toCollectionModel(entities);
    }

    protected PagedModel<T> getAllSortedAndPaginatedInternal(Pageable pageable) {
        Page<D> pageEntities = getService().findAll(pageable);
        int requestPageNumber = pageable.getPageNumber() + 1;
        if (requestPageNumber > pageEntities.getTotalPages()) {
            throw new MyPageNotFoundException(requestPageNumber,
                pageEntities.getTotalPages(), pageable.getPageSize());
        }
        return getPagedAssembler().toModel(pageEntities, getAssembler());
    }

    protected T getByIdInternal(int id) {
        D entity = getService().findById(id);
        return getAssembler().toModel(entity);
    }

    protected ResponseEntity<T> createInternal(T dto, HttpServletRequest request) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("ID have to be null");
        }
        D entity = getMapper().toEntity(dto);
        D savedEntity = getService().create(entity);
        T savedModel = getAssembler().toModel(savedEntity);
        ResponseUtil.addRedirectUrl(request, savedModel);
        URI location = ResponseUtil.getLocation(savedModel);
        return ResponseEntity.created(location).body(savedModel);
    }

    protected T updateInternal(int id, T dto, HttpServletRequest request) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("not ID in the request body");
        }
        if (dto.getId() != id) {
            throw new IllegalArgumentException(
                "ID in the request body have to be equal ID in URI");
        }
        D entity = getMapper().toEntity(dto);
        D updatedEntity = getService().update(id, entity);
        T updatedModel = getAssembler().toModel(updatedEntity);
        ResponseUtil.addRedirectUrl(request, updatedModel);
        return updatedModel;
    }

    protected void deleteInternal(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("illegal ID");
        }
        getService().delete(id);
    }

    protected abstract Service<D> getService();

    protected abstract RepresentationModelAssembler<D, T> getAssembler();

    protected abstract PagedResourcesAssembler<D> getPagedAssembler();

    protected abstract DtoMapper<D, T> getMapper();
}
