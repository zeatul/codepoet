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

import glz.hawk.codepoet.java.type.ArrayTypeName;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.TypeName;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawk.codepoet.java.type.ClassName.ofClass;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ParameterSpec {
    public final List<AnnotationInstanceSpec> annotations;
    public final Set<Modifier> modifiers;
    public final TypeName type;
    public final String name;

    private ParameterSpec(Builder builder) {
        this.annotations = Collections.unmodifiableList(builder.annotations);
        this.modifiers = Collections.unmodifiableSet((new LinkedHashSet<>(builder.modifiers)));
        this.type = builder.type;
        this.name = builder.name;
    }

    public static Builder builder(TypeName type, String name, Modifier... modifiers) {
        return new Builder(type, name).addModifiers(modifiers);
    }

    public static Builder builder(Class<?> clazz, String name, Modifier... modifiers) {
        return new Builder(ofClass(clazz), name).addModifiers(modifiers);
    }

    public Set<String> imports() {
        //type
        Set<String> imports = new HashSet<>(type.imports());
        //annotations
        this.annotations.stream().map(AnnotationInstanceSpec::imports).forEach(imports::addAll);
        return imports;
    }

    public void emit(JavaCodeWriter codeWriter, boolean varargs) throws IOException {
        codeWriter.emitAnnotations(annotations, true);
        codeWriter.emitModifiers(modifiers, ModifierSupport.legalParameterModifiers(), ModifierSupport.implicitParameterModifiers());
        if (varargs) {
            codeWriter.emit("$T... $L", ((ArrayTypeName) type).componentTypeName, name);
        } else {
            codeWriter.emit("$T $L", type, name);
        }
    }

    public static final class Builder {
        private final List<AnnotationInstanceSpec> annotations = new ArrayList<>();
        private final List<Modifier> modifiers = new ArrayList<>();
        private final TypeName type;
        private final String name;

        private Builder(TypeName type, String name) {
            this.type = argNotNull(type, "type");
            //TODO:校验name
            this.name = argNotBlank(name, "name");
        }

        public Builder addAnnotation(AnnotationInstanceSpec annotationSpec) {
            this.annotations.add(argNotNull(annotationSpec, "annotationSpec"));
            return this;
        }

        public Builder addAnnotation(ClassName annotationClassName) {
            this.annotations.add(AnnotationInstanceSpec.builder(annotationClassName).build());
            return this;
        }

        public Builder addAnnotation(Class<? extends Annotation> annotationClass) {
            this.annotations.add(AnnotationInstanceSpec.builder(annotationClass).build());
            return this;
        }

        public Builder addAnnotations(Iterable<AnnotationInstanceSpec> annotationSpecs) {
            for (AnnotationInstanceSpec annotationSpec : argNotNull(annotationSpecs, "annotationSpecs")) {
                addAnnotation(annotationSpec);
            }
            return this;
        }

        public Builder addAnnotations(AnnotationInstanceSpec... annotationSpecs) {
            return addAnnotations(Arrays.asList(annotationSpecs));
        }

        public Builder addModifiers(Modifier... modifiers) {
            return addModifiers(Arrays.asList(modifiers));
        }

        public Builder addModifiers(Iterable<Modifier> modifiers) {
            argNotNull(modifiers, "modifiers");
            int index = 0;
            for (Modifier modifier : modifiers) {
                this.modifiers.add(argNotNull(modifier, String.format("the %dth element in modifiers", ++index)));
            }
            return this;
        }

        public ParameterSpec build() {
            //TODO:校验
            return new ParameterSpec(this);
        }
    }
}
