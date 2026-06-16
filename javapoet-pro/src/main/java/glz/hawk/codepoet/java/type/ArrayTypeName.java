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
import java.lang.reflect.Type;
import java.util.Set;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ArrayTypeName implements TypeName {
    public final TypeName componentTypeName;

    private ArrayTypeName(TypeName componentTypeName) {
        this.componentTypeName = argNotNull(componentTypeName, "componentTypeName");
    }

    private ArrayTypeName(Type componentType) {
        this.componentTypeName = TypeNameHelper.ofType(argNotNull(componentType, "componentType"));
    }

    public static ArrayTypeName ofTypeName(TypeName componentTypeName) {
        return new ArrayTypeName(componentTypeName);
    }

    public static ArrayTypeName ofType(Type type) {
        return new ArrayTypeName(type);
    }

    public static ArrayTypeName ofClass(Class<?> clazz) {
        return new ArrayTypeName(ClassName.ofClass(clazz));
    }

    @Nonnull
    @Override
    public String keyword() {
        return String.format("%s[]", componentTypeName.keyword());
    }

    @Nonnull
    @Override
    public Set<String> imports() {
        return componentTypeName.imports();
    }
}
