package io.cinjug.microprofile.jaxrs.validation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.cinjug.microprofile.jaxrs.error.ErrorResponse;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

//----------------------------------------------------------------------------------------------------------------------
// ExceptionMapper Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(createErrorResponse(exception))
                .build();
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    private ErrorResponse createErrorResponse(ConstraintViolationException exception) {
        final List<String> errorMessages = exception.getConstraintViolations().stream()
                .map(this::toErrorMessage)
                .collect(Collectors.toList());
        return new ErrorResponse(errorMessages);
    }

    private String toErrorMessage(ConstraintViolation<?> e) {
        final String propertyPath = StreamSupport.stream(e.getPropertyPath().spliterator(), false)
                .filter(n -> ElementKind.PROPERTY == n.getKind())
                .map(Path.Node::getName)
                .collect(Collectors.joining("."));
        return String.format("%s: %s", propertyPath, e.getMessage());
    }
}
