/*
 * Copyright 2025-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package glz.hawk.codepoet.java.javadoc;

import glz.hawk.codepoet.java.javadoc.Javadoc.Location;

import java.util.*;

import static glz.hawk.codepoet.java.javadoc.Javadoc.Location.*;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This enum is responsible for
 *
 * @author Hawk
 */
public enum BlockTagType {
    AUTHOR("@author", false, Collections.singletonList(TYPE), 1000),
    PARAM("@param", true, Arrays.asList(CONSTRUCTOR, METHOD), 100),
    DEPRECATED("@deprecated", false, Arrays.asList(Location.values()), 900),
    RETURN("@return", false, Collections.singletonList(METHOD), 200),
    SINCE("@since", false, Arrays.asList(Location.values()), 500),
    SEE("@see", false, Arrays.asList(Location.values()), 600),
    THROWS("@throws", true, Arrays.asList(CONSTRUCTOR, METHOD), 300),
    VERSION("@version", false, Arrays.asList(Location.values()), 400);

    public final String type;
    public final boolean hasName;
    private final Set<Location> locations;
    private final Integer order;

    BlockTagType(String type, boolean hasName, List<Location> locations, int order) {
        this.type = type;
        this.hasName = hasName;
        this.locations = new HashSet<>(locations);
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public boolean support(Location location) {
        return locations.contains(argNotNull(location, "location"));
    }

}
