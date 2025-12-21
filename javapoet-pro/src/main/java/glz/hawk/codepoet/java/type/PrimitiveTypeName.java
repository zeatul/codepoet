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

package glz.hawk.codepoet.java.type;

import javax.annotation.Nonnull;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public enum PrimitiveTypeName implements TypeName {
    BOOLEAN("boolean"),
    BYTE("byte"),
    SHORT("short"),
    INT("int"),
    LONG("long"),
    CHAR("char"),
    FLOAT("float"),
    DOUBLE("double");
    private final String keyword;

    PrimitiveTypeName(String keyword) {
        this.keyword = keyword;
    }

    public static PrimitiveTypeName parse(String name) {
        switch (argNotBlank(name, "name")) {
            case "boolean":
                return BOOLEAN;
            case "byte":
                return BYTE;
            case "short":
                return SHORT;
            case "int":
                return INT;
            case "long":
                return LONG;
            case "float":
                return FLOAT;
            case "double":
                return DOUBLE;
            case "char":
                return CHAR;
            default:
                throw new IllegalArgumentException(String.format("No compatible PrimitiveTypeName found for the name: %s", name));
        }
    }

    @Nonnull
    @Override
    public String keyword() {
        return keyword;
    }
}
