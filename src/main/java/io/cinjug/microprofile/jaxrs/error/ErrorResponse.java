package io.cinjug.microprofile.jaxrs.error;

import java.util.List;

import lombok.Value;

@Value
public class ErrorResponse {

//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    List<String> errorMessages;
}
