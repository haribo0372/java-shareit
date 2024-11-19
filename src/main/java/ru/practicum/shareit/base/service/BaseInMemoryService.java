package ru.practicum.shareit.base.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.base.model.BaseModel;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
public abstract class BaseInMemoryService<T extends BaseModel> implements BaseService<T, Long> {
    private Long generatorId;
    private final HashMap<Long, T> storage;
    private Class<T> type;

    protected BaseInMemoryService(Class<T> type) {
        this.generatorId = 0L;
        this.storage = new HashMap<>();
        this.type = type;
    }

    @Override
    public Collection<T> findAll() {
        log.debug("Все {} возврашены", type.getName());
        return storage.values();
    }

    @Override
    public T findById(Long id) {
        return Optional.ofNullable(storage.get(id))
                .orElseThrow(() ->
                        new NotFoundException(format("%s по id=%s не найден", type.getName(), id)));
    }

    protected T save(T entity) {
        Long currentId = generateId();
        storage.put(currentId, entity);
        entity.setId(currentId);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        findById(id);
        storage.remove(id);
        log.info("{}{id={}} успешно удален", type.getName(), id);
    }

    private Long generateId() {
        return ++generatorId;
    }
}
