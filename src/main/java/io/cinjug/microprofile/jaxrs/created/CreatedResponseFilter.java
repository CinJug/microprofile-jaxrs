package io.cinjug.microprofile.jaxrs.created;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Created
public class CreatedResponseFilter implements ContainerResponseFilter {

//----------------------------------------------------------------------------------------------------------------------
// ContainerResponseFilter Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if(Response.Status.Family.SUCCESSFUL == responseContext.getStatusInfo().getFamily()) {
            responseContext.setStatusInfo(Response.Status.CREATED);
        }
    }
}
