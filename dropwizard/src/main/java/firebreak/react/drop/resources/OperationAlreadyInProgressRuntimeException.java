package firebreak.react.drop.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static java.lang.String.format;
import static javax.ws.rs.core.Response.status;

public class OperationAlreadyInProgressRuntimeException extends WebApplicationException {
    public OperationAlreadyInProgressRuntimeException(String chargeId) {
        super(acceptedResponse(format("Authorisation for charge already in progress, %s", chargeId)));
    }

    private static Response acceptedResponse(String format) {
        return status(Response.Status.ACCEPTED).entity(format).build();
    }
}
