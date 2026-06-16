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

import glz.hawk.codepoet.java.type.ClassName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.*;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for defining and emitting annotation instance.
 *
 * @author Hawk
 */
public class AnnotationInstanceSpec {
    public final ClassName type;
    public final Map<String, List<JavaCodeBlock>> members;

    private AnnotationInstanceSpec(Builder builder) {
        this.type = builder.type;
        this.members = Collections.unmodifiableMap(builder.members);
    }

    public static Builder builder(ClassName type) {
        return new Builder(type);
    }

    public static Builder builder(Class<? extends Annotation> clazz) {
        return new Builder(ClassName.ofClass(clazz));
    }

    Set<String> imports() {
        Set<String> imports = new HashSet<>(type.imports());
        members.values().stream().flatMap(List::stream).flatMap(JavaCodeBlock::importsAsStream).forEach(imports::add);
        return imports;
    }

    public void emit(JavaCodeWriter codeWriter) throws IOException {
        codeWriter.emit("@$T", type);
        if (members.isEmpty()) {
            return;
        }
        codeWriter.emit("(");
        if (members.size() == 1 && members.containsKey("value")) {
            emitAnnotationValues(codeWriter, members.get("value"));
        } else {
            int index = -1;
            for (Map.Entry<String, List<JavaCodeBlock>> entry : members.entrySet()) {
                if (++index > 0) {
                    codeWriter.emit(", ");
                }
                codeWriter.emit("$L = ", entry.getKey());
                emitAnnotationValues(codeWriter, entry.getValue());
            }
        }
        codeWriter.emit(")");
    }

    private void emitAnnotationValues(JavaCodeWriter codeWriter, List<JavaCodeBlock> codeBlocks) throws IOException {
        if (codeBlocks.size() > 1) {
            codeWriter.emit("{");
        }

        for (int i = 0; i < codeBlocks.size(); i++) {
            if (i > 0) {
                codeWriter.emit(", ");
            }
            codeWriter.emit(codeBlocks.get(i));
        }

        if (codeBlocks.size() > 1) {
            codeWriter.emit("}");
        }
    }


    public static final class Builder {
        public final Map<String, List<JavaCodeBlock>> members = new LinkedHashMap<>();
        private final ClassName type;

        private Builder(ClassName type) {
            this.type = argNotNull(type, "type");
        }

        public AnnotationInstanceSpec build() {
            return new AnnotationInstanceSpec(this);
        }

        public Builder addMember(String name, String format, Object... args) {
            return addMember(name, JavaCodeBlock.of(format, args));
        }

        public Builder addMember(String name, JavaCodeBlock codeBlock) {
            List<JavaCodeBlock> values = members.computeIfAbsent(name, k -> new ArrayList<>());
            values.add(codeBlock);
            return this;
        }

        public Builder addMember(String name, Object value) {
            argNotBlank(name, "name");
            argNotNull(value, "value");
            if (value instanceof Class || value instanceof ClassName) {
                return addMember(name, "$T.class", value);
            } else if (value instanceof CharSequence) {
                return addMember(name, "$S", argNotNull(((CharSequence) value).toString(), "value"));
            } else if (value instanceof Enum) {
                Class<?> enumClass = ((Enum<?>) value).getDeclaringClass();
                return addMember(name, "$T.$L", enumClass, ((Enum<?>) value).name());
            } else if (value instanceof AnnotationInstanceSpec ||
                value instanceof Boolean || value instanceof Byte || value instanceof Short || value instanceof Integer ||
                value instanceof Long || value instanceof Float || value instanceof Double || value instanceof Character) {
                return addMember(name, "$L", value);
            } else if (value.getClass().isArray()) {
                if (Array.getLength(value) == 0) {
                    addMember(name, "$L", "{}");
                } else {
                    Arrays.stream((Object[]) value).forEach(m -> addMember(name, m));
                }
                return this;
            } else if (value instanceof Iterable) {
                ((Iterable<?>) value).forEach(o -> addMember(name, o));
                return this;
            } else {
                throw new IllegalArgumentException(String.format("Annotation attribute doesn't support type: %s", value.getClass().getName()));
            }
        }

    }

}
