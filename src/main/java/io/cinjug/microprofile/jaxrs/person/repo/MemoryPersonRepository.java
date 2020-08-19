package io.cinjug.microprofile.jaxrs.person.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;

import io.cinjug.microprofile.jaxrs.person.model.Person;

@ApplicationScoped
public class MemoryPersonRepository implements PersonRepository {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final AtomicLong idGenerator = new AtomicLong();
    private final Executor executor = CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS, Executors.newFixedThreadPool(3));

    private final Map<String, Person> database = new ConcurrentHashMap<>();

//----------------------------------------------------------------------------------------------------------------------
// PersonRepository Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public String add(Person person) {
        final String id = String.valueOf(idGenerator.incrementAndGet());
        person.setId(id);
        database.put(id, person);
        return id;
    }

    @Override
    public List<Person> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public CompletionStage<List<Person>> findAllAsync() {
        return CompletableFuture.supplyAsync(this::findAll, executor);
    }

    @Override
    public Person findById(String id) {
        return database.get(id);
    }
}
