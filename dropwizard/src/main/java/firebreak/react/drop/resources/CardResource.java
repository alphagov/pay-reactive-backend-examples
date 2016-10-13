package firebreak.react.drop.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class CardResource {

    @GET
    public Response rootResource() {
        return Response.ok("{\"message\":\"firebreak rocks\"}").build();
    }


}
