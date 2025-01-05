package ru.practicum.shareit.base.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.base.model.BaseModel;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;

import static java.lang.String.format;

@Slf4j
public abstract class BaseInDbService<T extends BaseModel<Long>, R extends JpaRepository<T, Long>>
        implements BaseService<T, Long> {
    protected final R repository;
    private final String entityNameForLog;

    public BaseInDbService(R repository, String entityNameForLog) {
        this.repository = repository;
        this.entityNameForLog = entityNameForLog;
    }

    @Override
    public Collection<T> findAll() {
        log.debug("Все {} возврашены", entityNameForLog);
        return repository.findAll();
    }

    @Override
    public T findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(format("%s по id=%s не найден", entityNameForLog, id)));
    }

    protected T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        findById(id);
        repository.deleteById(id);
        log.info("{}{id={}} успешно удален", entityNameForLog, id);
    }
}
