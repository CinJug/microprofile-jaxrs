package io.cinjug.microprofile.jaxrs.person.loader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.cinjug.microprofile.jaxrs.person.model.Person;
import io.cinjug.microprofile.jaxrs.person.repo.PersonRepository;

@ApplicationScoped
public class PersonLoader {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final PersonRepository repository;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    @Inject
    public PersonLoader(PersonRepository repository) {
        this.repository = repository;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    public void loadData(@Observes @Initialized(ApplicationScoped.class) Object init) {
        repository.add(Person.builder()
                .firstName("Slappy")
                .lastName("White")
                .build());
    }
}
