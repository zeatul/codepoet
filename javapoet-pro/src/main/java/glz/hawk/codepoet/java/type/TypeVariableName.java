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
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class TypeVariableName implements TypeName {
    public final List<TypeName> bounds = new ArrayList<>();
    private final String name;

    private TypeVariableName(String name) {
        this.name = argNotBlank(name, "name");
    }

    public static TypeVariableName of(String name) {
        return new TypeVariableName(name);
    }

    public static TypeVariableName of(String name, TypeName... bounds) {
        return of(name, Arrays.asList(bounds));
    }

    public static TypeVariableName of(String name, Class<?>... bounds) {
        return of(name, Stream.of(bounds).map(ClassName::ofClass).collect(Collectors.toList()));
    }

    public static TypeVariableName of(String name, Iterable<TypeName> bounds) {
        TypeVariableName typeVariableName = new TypeVariableName(name);
        if (ObjectHelper.isEmpty(bounds)) {
            return typeVariableName;
        }
        int index = -1;
        for (TypeName typeName : bounds) {
            typeVariableName.addBound(typeName, ++index);
        }
        return typeVariableName;
    }

    public static TypeVariableName of(TypeVariable typeVariable) {
        TypeParameterElement element = (TypeParameterElement) typeVariable.asElement();
        String name = element.getSimpleName().toString();
        return TypeVariableName.of(name, element.getBounds().stream().map(TypeNameHelper::ofTypeMirror).toArray(TypeName[]::new));
    }

    @Nonnull
    @Override
    public List<String> imports() {
        List<String> imports = new ArrayList<>();
        bounds.stream().map(TypeName::imports).forEach(imports::addAll);
        return imports;
    }

    @Nonnull
    @Override
    public String keyword() {
        return name;
    }

    private void addBound(TypeName typeName, final int index) {
        argNotNull(typeName, () -> String.format("The %dth element is null in bounds.", index));
        if (typeName instanceof TypeVariableName) {
            if (index == 0) {
                this.bounds.add(typeName);
            } else {
                throw new IllegalArgumentException("TypeVariable must be the first bound in bounds.");
            }
        } else if (typeName instanceof ClassName || typeName instanceof ParameterizedTypeName) {
            this.bounds.add(typeName);
        } else {
            throw new IllegalArgumentException(String.format("The %dth element is illegal in bounds, its type is %s", index, typeName.getClass().getSimpleName()));
        }
    }

}
