package io.cinjug.microprofile.jaxrs.person;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import io.cinjug.microprofile.jaxrs.created.Created;
import io.cinjug.microprofile.jaxrs.gzip.ZipIt;
import io.cinjug.microprofile.jaxrs.logging.Logged;
import io.cinjug.microprofile.jaxrs.person.model.Person;
import io.cinjug.microprofile.jaxrs.person.proto.Protobufs;
import io.cinjug.microprofile.jaxrs.person.repo.PersonRepository;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("persons")
@Dependent
public class PersonResource {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final PersonRepository repository;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    @Inject
    public PersonResource(PersonRepository repository) {
        this.repository = repository;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @POST
    @Consumes(APPLICATION_JSON)
    @Created
    public String createPerson(@Valid Person person) {
        return repository.add(person);
    }

    @GET
    @Path("events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void events(@Context SseEventSink eventSink, @Context Sse sse, @HeaderParam("Last-Event-ID") @DefaultValue("0") int lastEventId) {
        repository.findAllAsync()
                .thenAcceptAsync(list -> {
                    try (SseEventSink sink = eventSink) {
                        list.stream()
                                .filter(p -> Integer.parseInt(p.getId()) > lastEventId)
                                .findFirst()
                                .ifPresent(person -> {
                                    final String fullName = person.getFirstName() + " " + person.getLastName();
                                    final OutboundSseEvent event = sse.newEventBuilder()
                                            .id(person.getId())
                                            .data(fullName)
                                            .name("person")
                                            .build();
                                    sink.send(event);
                                });
                    }
                });
    }

    @GET
    @Produces(APPLICATION_JSON)
    @ZipIt
    public GetEverybodyResponse getEverybody() {
        return GetEverybodyResponse.builder()
                .persons(repository.findAll())
                .build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("future")
    @ZipIt
    public CompletionStage<GetEverybodyResponse> getEverybodyFuture() {
        return repository.findAllAsync()
                .thenApply(GetEverybodyResponse::new);
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("suspended")
    @ZipIt
    public void getEverybodySuspended(@Suspended AsyncResponse response) {
        response.setTimeout(10, TimeUnit.SECONDS);
        response.setTimeoutHandler(r -> r.resume(GetEverybodyResponse.builder()
                .persons(List.of())
                .build()));

        repository.findAllAsync()
                .thenApply(GetEverybodyResponse::new)
                .thenAccept(response::resume)
                .exceptionally(t -> {
                    response.resume(t);
                    return null;
                });
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("{id}")
    @ZipIt
    @Logged
    public Person getPerson(@PathParam("id") String id) {
        return repository.findById(id);
    }

    @GET
    @Path("{id}/proto")
    @Produces("application/protobuf")
    public Protobufs.Person getPersonProto(@PathParam("id") String id) {
        return ofNullable(repository.findById(id))
                .map(person -> Protobufs.Person.newBuilder()
                        .setId(person.getId())
                        .setFirstName(person.getFirstName())
                        .setLastName(person.getLastName())
                        .build())
                .orElse(null);
    }

    @GET
    @Path("streamy")
    @Produces(TEXT_PLAIN)
    public StreamingOutput streamIt() {
        return output -> output.write("Hello, World!".getBytes(StandardCharsets.UTF_8));
    }
}
