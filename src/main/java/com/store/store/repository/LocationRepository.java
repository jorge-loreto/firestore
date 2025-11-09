package com.store.store.repository;

import java.util.List;
import java.util.Optional;

import com.store.store.model.Location;

public interface LocationRepository {

    Location save(Location location);

    Boolean deleteAll();

    Boolean deleteById(String id);

    long count();

    Optional<Location> findById(String id);

    Iterable<Location> findAllById(Iterable<String> ids);

    List<Location> findAll();

    Iterable<Location> saveAll(Iterable<Location> locations);
}
