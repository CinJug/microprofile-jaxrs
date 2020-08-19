package io.cinjug.microprofile.jaxrs.person;

import java.util.List;

import io.cinjug.microprofile.jaxrs.person.model.Person;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GetEverybodyResponse {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    List<Person> persons;
}
