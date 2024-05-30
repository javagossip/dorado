/*
 *
 *  * Copyright 2017 The OpenDSP Project
 *  *
 *  * The OpenDSP Project licenses this file to you under the Apache License,
 *  * version 2.0 (the "License"); you may not use this file except in compliance
 *  * with the License. You may obtain a copy of the License at:
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *
 */

package ai.houyi.dorado.rest.router;

import java.util.List;

import org.junit.After;
import org.junit.Test;

import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.HttpMethod;
import ai.houyi.dorado.rest.annotation.Path;

import static org.junit.Assert.*;

public class RouterTest {

    @After
    public void resetRouter() {
        Router.getInstance().reset();
    }

    @Test
    public void addRoute() {
        Router router = Router.getInstance();
        String path = "/api/v1/users/{id:\\d+}";
        String path1 = "/api/v1/users";
        String path2 = "/api/v1/users/{id:\\d+}/details";

        router.addRoute(new Route(path, HttpMethod.GET));
        router.addRoute(new Route(path1, HttpMethod.POST));
        router.addRoute(new Route(path2, HttpMethod.GET));
        router.addRoute(new Route("/api/v1/campaigns/{id}", null));

        Route route = router.getRoute("/api/v1/users/123", HttpMethod.GET);
        assertNotNull(route);
        assertEquals("/api/v1/users/{id:\\d+}", route.getPath());

        Route route1 = router.getRoute("/api/v1/users", HttpMethod.POST);
        assertNotNull(route1);
        assertEquals("/api/v1/users", route1.getPath());

        Route route2 = router.getRoute("/api/v1/campaigns/1", HttpMethod.GET);
        assertNotNull(route2);
        assertEquals("/api/v1/campaigns/{id}", route2.getPath());
        assertEquals("*", route2.getMethod());

        route2 = router.getRoute("/api/v1/campaigns/2", "*");
        assertNotNull(route2);

        assertNotNull(route);
        assertNotNull(route1);

        route2 = router.getRoute("/api/v1/users/7/details", HttpMethod.GET);
        assertNotNull(route2);

        route2 = router.getRoute("/api/v1/users/7/detail", HttpMethod.GET);
        assertNull(route2);
    }

    @Test
    public void getRouteWithPathVariableWithRegExp() {
        Router router = Router.getInstance();
        router.addRoute(new Route("/api/v1/campaigns/{id:\\d+}", null));

        Route route = router.getRoute("/api/v1/campaigns/123", HttpMethod.GET);
        assertNotNull(route);
        assertEquals("/api/v1/campaigns/{id:\\d+}", route.getPath());
        route = router.getRoute("/api/v1/campaigns/solo", HttpMethod.GET);
        assertNull(route);
    }

    @Test
    public void getRouteWithPathIsRegExp() {
        Router router = Router.getInstance();
        router.addRoute(new Route("/api-docs/swagger\\..*", HttpMethod.GET));

        Route route = router.getRoute("/api-docs/swagger.json", HttpMethod.GET);
        assertNotNull(route);
        assertEquals("/api-docs/swagger\\..*", route.getPath());
    }

    @Test
    public void registerRoutesByType() {
        Router router = Router.getInstance();
        router.registerRoutesByType(DemoController.class);

        List<Route> routes = router.getRoutes();
        assertEquals(2, routes.size());

        Route route = router.getRoute("/demo", HttpMethod.GET);
        assertNotNull(route);
        assertEquals("/demo", route.getPath());
        route = router.getRoute("/demo/api", HttpMethod.POST);
        assertNotNull(route);
        assertEquals("/demo/api", route.getPath());
    }

    @Controller
    public static class DemoController {

        @Path("/demo")
        public String demo() {
            return "demo";
        }

        @Path("/demo/api")
        public String apiDemo() {
            return "apiDemo";
        }
    }
}