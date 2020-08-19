package io.cinjug.microprofile.jaxrs.person.repo;

import java.util.List;
import java.util.concurrent.CompletionStage;

import io.cinjug.microprofile.jaxrs.person.model.Person;

public interface PersonRepository {

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    String add(Person person);

    List<Person> findAll();

    CompletionStage<List<Person>> findAllAsync();

    Person findById(String id);
}
