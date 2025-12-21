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
import javax.lang.model.type.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class WildcardTypeName implements TypeName {
    public final List<TypeName> upperBounds = new ArrayList<>();
    public final List<TypeName> lowerBounds = new ArrayList<>();

    private WildcardTypeName(List<TypeName> upperBounds, List<TypeName> lowerBounds) {
        copyBounds(upperBounds, this.upperBounds, "upperBounds");
        copyBounds(lowerBounds, this.lowerBounds, "lowerBounds");
    }

    public static WildcardTypeName of() {
        return new WildcardTypeName(Collections.emptyList(), Collections.emptyList());
    }

    public static WildcardTypeName of(List<TypeName> upperBounds, List<TypeName> lowerBounds) {
        return new WildcardTypeName(upperBounds, lowerBounds);
    }

    public static WildcardTypeName ofUpper(TypeName typeName) {
        return new WildcardTypeName(Collections.singletonList(argNotNull(typeName, "typeName")), Collections.emptyList());
    }

    public static WildcardTypeName ofUpper(Class<?> clazz) {
        return ofUpper(ClassName.ofClass(clazz));
    }

    public static WildcardTypeName ofLower(TypeName typeName) {
        return new WildcardTypeName(Collections.emptyList(), Collections.singletonList(argNotNull(typeName, "typeName")));
    }

    public static WildcardTypeName ofLower(Class<?> clazz) {
        return ofLower(ClassName.ofClass(clazz));
    }

    public static WildcardTypeName of(WildcardType wildcardType) {
        if (wildcardType.getExtendsBound() != null) {
            return WildcardTypeName.ofUpper(TypeNameHelper.ofTypeMirror(wildcardType.getExtendsBound()));
        }else if (wildcardType.getSuperBound() != null){
            return WildcardTypeName.ofLower(TypeNameHelper.ofTypeMirror(wildcardType.getSuperBound()));
        }else{
            return WildcardTypeName.of();
        }
    }

    @Nonnull
    @Override
    public List<String> imports() {
        List<String> imports = new ArrayList<>();
        this.upperBounds.stream().map(TypeName::imports).forEach(imports::addAll);
        this.lowerBounds.stream().map(TypeName::imports).forEach(imports::addAll);
        return imports;
    }

    private void copyBounds(List<TypeName> sourceBounds, List<TypeName> targetBounds, String boundsName) {
        if (ObjectHelper.isEmpty(sourceBounds)) {
            return;
        }
        int index = -1;
        for (TypeName typeName : sourceBounds) {
            addBound(targetBounds, typeName, ++index, boundsName);
        }
    }

    private void addBound(List<TypeName> targetBounds, TypeName typeName, int index, String boundsName) {
        argNotNull(typeName, () -> String.format("The %dth element is null in %s", index, boundsName));
        if (typeName instanceof TypeVariableName) {
            if (index == 0) {
                targetBounds.add(typeName);
            } else {
                throw new IllegalArgumentException(String.format("TypeVariable must be the first bound in %s.", boundsName));
            }
        } else if (typeName instanceof ClassName || typeName instanceof ParameterizedTypeName) {
            targetBounds.add(typeName);
        } else {
            throw new IllegalArgumentException(String.format("The %dth element is illegal in %s, its type is %s", index, boundsName, typeName.getClass().getSimpleName()));
        }
    }

    @Nonnull
    @Override
    public String keyword() {
        StringBuilder sb = new StringBuilder("?");
        if (!upperBounds.isEmpty() && !lowerBounds.isEmpty()) {
            throw new IllegalStateException("Wildcard can't have upper bound and lower bound in the same time.");
        }
        if (!upperBounds.isEmpty()) {
            if (upperBounds.size() > 1) {
                throw new IllegalStateException("Wildcard only can have one lower bound");
            }
            sb.append(" extends ").append(upperBounds.get(0).keyword());
        } else if (!lowerBounds.isEmpty()) {
            if (lowerBounds.size() > 1) {
                throw new IllegalStateException("Wildcard only can have one lower bound");
            }
            sb.append(" super ").append(lowerBounds.get(0).keyword());
        }
        return sb.toString();
    }
}
