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

import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.http.HttpResponse;

import java.util.Map;

public interface RouteHandler {
    RouteHandler DUMMY = new RouteHandler() {

        @Override
        public String toString() {
            return "<<DUMMY>>";
        }

        @Override
        public void handle(HttpRequest request, HttpResponse response, Map<String, String> pathVars) {
            //
        }
    };
    /**
     * @param request The http request
     * @param response the http response
     * @param pathVars the path parameters, eg: id for /user/:id
     */
    void handle(HttpRequest request, HttpResponse response, Map<String, String> pathVars);
}
