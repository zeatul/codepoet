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

package glz.hawk.codepoet.java;

import glz.hawk.codepoet.java.type.TypeName;
import glz.hawkframework.core.support.ArgumentSupport;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class AnnotatedTypeName implements TypeName {

    private final TypeName typeName;
    private final List<AnnotationInstanceSpec> annotations;

    private AnnotatedTypeName(TypeName typeName, List<AnnotationInstanceSpec> annotations) {
        this.typeName = argNotNull(typeName, "typeName");
        this.annotations = ArgumentSupport.argNoNullElement(argNotNull(annotations, "annotations"), "annotations");
    }

    public static AnnotatedTypeName of(TypeName typeName, List<AnnotationInstanceSpec> annotations) {
        return new AnnotatedTypeName(typeName, annotations);
    }

    public static AnnotatedTypeName of(TypeName typeName, AnnotationInstanceSpec... annotations) {
        return new AnnotatedTypeName(typeName, Arrays.asList(annotations));
    }

    @Nonnull
    @Override
    public String keyword() {
        if (annotations.isEmpty()) {
            return typeName.keyword();
        } else {
            StringBuilder sb = new StringBuilder();
            JavaCodeWriter writer = new JavaCodeWriter(sb, "IllegalIndent", "'IllegalLineSeparator'");
            try {
                writer.emitAnnotations(annotations, true);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            sb.append(typeName.keyword());
            return sb.toString();
        }
    }

    @Nonnull
    @Override
    public Set<String> imports() {
        HashSet<String> imports = new HashSet<>(typeName.imports());
        annotations.stream().flatMap(a -> a.imports().stream()).forEach(imports::add);
        return imports;
    }
}
