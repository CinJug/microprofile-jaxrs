package io.cinjug.microprofile.jaxrs.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
@ZipIt
public class GzipWriterInterceptor implements WriterInterceptor {

//----------------------------------------------------------------------------------------------------------------------
// WriterInterceptor Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        context.getHeaders().add(HttpHeaders.CONTENT_ENCODING, "gzip");
        final OutputStream original = context.getOutputStream();
        try (GZIPOutputStream zipStream = new GZIPOutputStream(original)) {
            context.setOutputStream(zipStream);
            context.proceed();
        } finally{
            context.setOutputStream(original);
        }
    }
}
