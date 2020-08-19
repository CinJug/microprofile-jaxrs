package io.cinjug.microprofile.jaxrs.logging;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;

import lombok.RequiredArgsConstructor;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class RequestLogFilter implements ContainerRequestFilter, ContainerResponseFilter {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final TimeUnit ELAPSED_TIME_UNIT = TimeUnit.SECONDS;

    public static final String START_NANO_TIME = "startNanoTime";
    private static final double DIVISOR = TimeUnit.NANOSECONDS.convert(1, ELAPSED_TIME_UNIT);

    private final Logger logger;

//----------------------------------------------------------------------------------------------------------------------
// ContainerRequestFilter Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty(START_NANO_TIME, System.nanoTime());
    }

//----------------------------------------------------------------------------------------------------------------------
// ContainerResponseFilter Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        ofNullable((Long) requestContext.getProperty(START_NANO_TIME)).ifPresent(startNanoTime -> {
            final long elapsedNanos = System.nanoTime() - startNanoTime;
            final Response.StatusType statusInfo = responseContext.getStatusInfo();
            logger.info(() -> String.format("%s %s - %d %s (%1.3f %s)",
                    requestContext.getMethod(),
                    requestContext.getUriInfo().getAbsolutePath().getPath(),
                    statusInfo.getStatusCode(),
                    statusInfo.getReasonPhrase(),
                    elapsedNanos / DIVISOR,
                    ELAPSED_TIME_UNIT.name().toLowerCase()));
        });
    }
}
