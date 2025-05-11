package ai.houyi.dorado.example.controller;

import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.GET;
import ai.houyi.dorado.rest.annotation.Path;

@Controller
@Path("/benchmark")
public class BenchmarkController {
    @Path("/echo")
    @GET
    public String echo(String ping) {
        return "pong: " + ping;
    }
}
