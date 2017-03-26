package helloworld.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by qdnguyen on 3/26/17.
 */

@Path("/")
public class HelloWorldApi {
    private static final Log logger = LogFactory.getLog(HelloWorldApi.class);
    private Config config;

    public HelloWorldApi (Config config) {
        this.config = config;
    }

    @GET
    @Path("/helloworld")
    @Produces(MediaType.TEXT_PLAIN)
    public Response helloHandler() {
        return Response.status(200).entity("Hello World").build();
    }
}
