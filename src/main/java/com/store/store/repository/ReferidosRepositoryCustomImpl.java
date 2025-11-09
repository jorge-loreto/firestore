package com.store.store.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveOperations;
import com.store.store.model.Referidos;
import com.store.store.model.Status;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Comparator;
import java.util.Locale;

@Repository
public class ReferidosRepositoryCustomImpl implements ReferidosRepositoryCustom {

    private final FirestoreReactiveOperations firestoreReactiveOperations;

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d/M/yyyy, h:mm:ss a")
            .toFormatter(new Locale("es", "MX"));

    public ReferidosRepositoryCustomImpl(FirestoreReactiveOperations firestoreReactiveOperations) {
        this.firestoreReactiveOperations = firestoreReactiveOperations;
    }

    @Override
    public Flux<Referidos> findByStatusOrdered(Status status) {
        return firestoreReactiveOperations.findAll(Referidos.class)
                .filter(r -> r.getStatus() == status)
                .sort(Comparator
                        .comparing(Referidos::getPlantel, Comparator.nullsLast(String::compareTo))
                        .thenComparing(r -> parseDate(r.getCreationDate()),
                                Comparator.nullsLast(LocalDateTime::compareTo)));
    }

    private LocalDateTime parseDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.isEmpty())
                return null;
            String cleaned = dateStr.trim().replace(".", ""); // remove dots from "p.m."
            return LocalDateTime.parse(cleaned, FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
}
