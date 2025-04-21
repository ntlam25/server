package com.example.crabfood_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.crabfood_api.exception.ResourceNotFoundException;


public abstract class AbstractCrudService<Q, R, RP extends JpaRepository<D, I>, D, I> implements BaseCRUDService<Q, R, D, I> {

    protected RP repository;
    protected final Class<D> domainClass;

    protected AbstractCrudService(RP repository, java.lang.Class<D> domainClass) {
        this.repository = repository;
        this.domainClass = domainClass;
    }

    @Transactional
    @Override
    public R create(Q request) {
        D domainEntity = createAndSave(request);
        return toResponse(domainEntity);
    }

    protected abstract D createAndSave(Q request);

    @Override
    public R findById(I id) {  
        D domainEntity = repository.findById(id).orElseThrow(() -> 
            new ResourceNotFoundException("Could not found " + domainClass.getSimpleName() + " with id " + id));
        return toResponse(domainEntity);
    }

    @Transactional
    @Override
    public R update(I id, Q request) {
        D domainEntity = updateAndSave(id, request);
        return toResponse(domainEntity);
    }

    protected abstract D updateAndSave(I id, Q request);

    @Transactional
    @Override
    public void deleteById(I id) {
        D domainEntity = repository.findById(id).orElseThrow(() -> 
            new ResourceNotFoundException("Could not found " + domainClass.getSimpleName() + " with id " + id));
        repository.delete(domainEntity);
    }

    protected abstract R toResponse(D domainEntity);

    @Override
    public List<R> findAll() {
        List<D> domainEntities = repository.findAll();
        return domainEntities.stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public List<R> findAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortBy);
        if (sortDir.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        Page<D> domainEntities = repository.findAll(PageRequest.of(page, size, sort));
        return domainEntities.getContent().stream()
            .map(this::toResponse)
            .toList();
    }
}
