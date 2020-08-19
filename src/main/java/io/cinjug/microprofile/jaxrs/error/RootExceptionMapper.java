package io.cinjug.microprofile.jaxrs.error;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RootExceptionMapper implements ExceptionMapper<Exception> {

//----------------------------------------------------------------------------------------------------------------------
// ExceptionMapper Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Response toResponse(Exception exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(exception.getMessage())
                .build();
    }
}
