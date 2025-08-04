package com.store.store.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.store.store.model.Location;

public interface LocationRepository extends FirestoreReactiveRepository<Location> {
}
