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

import glz.hawkframework.core.helper.ObjectHelper;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ParameterizedTypeName implements TypeName {

    public final ClassName rawType;
    public final List<TypeName> typeArguments = new ArrayList<>();

    private ParameterizedTypeName(ClassName rawType, List<TypeName> typeArguments) {
        this.rawType = argNotNull(rawType, "rawType");
        int index = -1;
        if (ObjectHelper.isNotEmpty(typeArguments)) {
            for (TypeName typeName : typeArguments) {
                argNotNull(typeName, String.format("The %dth element in typeArguments is null.", ++index));
                this.typeArguments.add(typeName);
            }

        }
    }

    public static ParameterizedTypeName of(ClassName rawType, List<TypeName> typeArguments) {
        return new ParameterizedTypeName(rawType, typeArguments);
    }

    public static ParameterizedTypeName of(ClassName rawType, TypeName... typeArguments) {
        return new ParameterizedTypeName(rawType, Arrays.asList(typeArguments));
    }

    public static ParameterizedTypeName of(Class<?> rawType, TypeName... typeArguments) {
        return new ParameterizedTypeName(ClassName.ofClass(rawType), Arrays.asList(typeArguments));
    }

    public static ParameterizedTypeName of(Class<?> rawType, Class<?>... typeArguments) {
        return new ParameterizedTypeName(ClassName.ofClass(rawType), Stream.of(typeArguments).map(ClassName::ofClass).collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public String keyword() {
        StringBuilder sb = new StringBuilder();
        sb.append(rawType.keyword());
        if (typeArguments.isEmpty()) {
            return sb.toString();
        }
        sb.append("<");
        for (int i = 0; i < typeArguments.size(); i++) {
            sb.append(typeArguments.get(i).keyword());
            if (i < typeArguments.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(">");
        return sb.toString();
    }

    @Nonnull
    @Override
    public Set<String> imports() {
        Set<String> result = new HashSet<>(rawType.imports());
        typeArguments.stream().map(TypeName::imports).forEach(result::addAll);
        return result;
    }
}
