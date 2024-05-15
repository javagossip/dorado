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

package ai.houyi.dorado.rest.router.trie;

import ai.houyi.dorado.rest.annotation.HttpMethod;

import org.junit.Test;

import static org.junit.Assert.*;

public class RouterTest {

    @Test
    public void addRoute() {
        Router router = Router.getInstance();
        String path = "/api/v1/users/{id:\\d+}";
        String path1 = "/api/v1/users";
        router.addRoute(new Route(path, HttpMethod.GET));
        router.addRoute(new Route(path1, HttpMethod.POST));
        router.addRoute(new Route("/api/v1/campaigns/{id}", null));

        Route route = router.matchRoute("/api/v1/users/123", HttpMethod.GET);
        Route route1 = router.matchRoute("/api/v1/users", HttpMethod.POST);

        Route route2 = router.matchRoute("/api/v1/campaigns/1", HttpMethod.GET);
        assertNull(route2);
        route2 = router.matchRoute("/api/v1/campaigns/2", "_ANY_");
        assertNotNull(route2);

        assertNotNull(route);
        assertNotNull(route1);
    }

    @Test
    public void getRoute() {
    }
}