package magnus.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/none-secured")
@Produces(MediaType.APPLICATION_JSON)
public class DemoResource {

    @GET
    @Path("/{id}")
    public String getDemo(@PathParam("id") int id) {
        return "hello, demo " + id;
    }
}
