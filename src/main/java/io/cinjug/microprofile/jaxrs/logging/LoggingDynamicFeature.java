package io.cinjug.microprofile.jaxrs.logging;

import java.util.logging.Logger;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import static java.util.Optional.ofNullable;

@Provider
public class LoggingDynamicFeature implements DynamicFeature {

//----------------------------------------------------------------------------------------------------------------------
// DynamicFeature Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        ofNullable(resourceInfo.getResourceMethod().getAnnotation(Logged.class))
                .map(Logged::value)
                .map(loggerName -> "".equals(loggerName) ? resourceInfo.getResourceClass().getCanonicalName() : loggerName)
                .ifPresent(loggerName -> context.register(new RequestLogFilter(Logger.getLogger(loggerName))));
    }
}
