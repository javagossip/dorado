/*
 * Copyright 2017 The OpenDSP Project
 *
 * The OpenDSP Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ai.houyi.dorado.rest.util;

/**
 * @author wangwp
 */
public final class Constant {

    public static final int NCPU = Runtime.getRuntime().availableProcessors();

    public static final int DEFAULT_SEND_BUFFER_SIZE = 256 * 1024;

    public static final int DEFAULT_RECV_BUFFER_SIZE = 256 * 1024;

    public static final int DEFAULT_CONNECTION_SIZE = 1;

    public static final int DEFAULT_ACCEPTOR_COUNT = NCPU * 2;

    public static final int DEFAULT_IO_WORKER_COUNT = NCPU * 2;

    public static final int DEFAULT_CONNECT_TIMEOUT = 3000;

    public static final int DEFAULT_SO_TIMEOUT = 3000;

    public static final int DEFAULT_MIN_WORKER_THREAD = 100;

    public static final int DEFAULT_MAX_WORKER_THREAD = 100;

    public static final int DEFAULT_BACKLOG = 1024;

    public static final int DEFAULT_MAX_PENDING_REQUEST = 10000;

    public static final String SIGN_COLON = ":";

    public static final int DEFAULT_MAX_IDLE_TIME = 10;

    public static final int MAX_FRAME_LENGTH = 1048576 * 5;

    public static final String SERVER_NAME = "Dorado";

    public static final int DEFAULT_MAX_PACKET_LENGTH = 1024 * 1024;

    public static final String BLANK_STRING = "";

    public static final int DEFAULT_LISTEN_PORT = 8080;

    public static final String CLASS_SUFFIX = ".class";

    public static final int CLASS_SUFFIX_LENGTH = 6;

    public static final String DOT = ".";

    public static final String SLASH = "/";
}
