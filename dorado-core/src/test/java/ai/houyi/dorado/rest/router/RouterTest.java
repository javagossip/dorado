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

import ai.houyi.dorado.exception.DoradoException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

public class RouterTest {
    private Router router;

    @Before
    public void setUp() throws Exception {
        router = Router.newInstance();
    }

    @Test
    public void matchRoute_NotFound() {
        router.addRoute("/campaigns/{id}", "GET", null);
        Router.RouteContext routeContext = router.matchRoute("/campaigns", "GET");
        assertNull(routeContext);

        routeContext = router.matchRoute("/campaigns/abc", "POST");
        assertNull(routeContext);
    }

    @Test
    public void addRoute() {
        router.addRoute("/campaigns/{id}", "GET", null);
        router.addRoute("/campaigns/{id:[0-9]+}", "GET", null);
        router.addRoute("/campaigns/123", "GET", null);
        router.addRoute("/campaigns/123/details", "GET", null);

        Router.RouteContext routeContext = router.matchRoute("/campaigns/123", "GET");
        assertTrue(routeContext.pathVars.isEmpty());
        //匹配/campaigns/{id}
        routeContext = router.matchRoute("/campaigns/abc", "GET");
        assertTrue(routeContext != null);
        assertTrue(routeContext.pathVars.containsKey("id"));

        //匹配/campaigns/{id:[0-9]+}
        routeContext = router.matchRoute("/campaigns/789", "GET");
        assertTrue(routeContext != null);
        assertTrue(routeContext.pathVars.containsKey("id"));
        assertEquals("789", routeContext.pathVars.get("id"));

        //匹配不到的验证
        routeContext = router.matchRoute("/campaigns/abc/123", "GET");
        assertNull(routeContext);

        router.dump();
    }

    @Test
    public void addRoute_conflict() {
        router.addRoute("/campaigns/{id}", "GET", null);
        try {
            router.addRoute("/campaigns/{name}", "GET", null);
            fail();
        } catch (DoradoException ex) {
            assertTrue(ex.getMessage().contains("Route conflict"));
            assertTrue(ex.getMessage().contains("/campaigns/{name}"));
        }

        router.addRoute("/campaigns/{id:[A-Za-z0-9]+}", "GET", null);
        try {
            router.addRoute("/campaigns/{name:[A-Za-z0-9]+}", "GET", null);
            fail();
        } catch (DoradoException ex) {
            assertTrue(ex.getMessage().contains("Route conflict"));
            assertTrue(ex.getMessage().contains("/campaigns/{name:[A-Za-z0-9]+}"));
        }
    }

    @Test
    public void addRoute_with_unsupported_method() {
        try {
            router.addRoute("/campaigns/{id}", "OPTIONS", null);
            fail();
        } catch (DoradoException ex) {
            assertTrue(ex.getMessage().contains("Unsupported http method"));
        }
    }

    @Test
    public void pathVar_regex() {
        Router.PathVar pathVar = Router.PathVar.create("{id:[0-9]+}");
        assertTrue(pathVar.regex.equals("[0-9]+"));
        assertTrue(pathVar.match("123"));
        assertEquals("id", pathVar.name);

        pathVar = Router.PathVar.create("{id}");
        assertNull(pathVar.regex);
        assertEquals("id", pathVar.name);
    }
}