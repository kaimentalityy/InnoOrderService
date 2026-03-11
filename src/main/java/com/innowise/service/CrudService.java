package com.innowise.service;

/**
 * Generic CRUD service interface.
 *
 * @param <T>  DTO type returned by the service
 * @param <ID> Type of the entity identifier (e.g., Long)
 */
public interface CrudService<T, ID> {

    T create(T createDto);

    T update(ID id, T updateDto);

    void delete(ID id);

    T findById(ID id);
}
