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

import glz.hawk.codepoet.java.javadoc.AttributeJavadoc;
import glz.hawk.codepoet.java.javadoc.Javadoc;

import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Stream;

import static glz.hawkframework.core.support.ArgumentSupport.argElementNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
@SuppressWarnings("ALL")
public abstract class MemberSpec<K extends Javadoc> {
    public final K javadoc;
    public final List<AnnotationInstanceSpec> annotations;
    public final Set<Modifier> modifiers;

    protected MemberSpec(MemberBuilder<?, K> builder) {
        this.javadoc = builder.javadoc;
        this.annotations = Collections.unmodifiableList(builder.annotations);
        this.modifiers = Collections.unmodifiableSet((new LinkedHashSet<>(builder.modifiers)));
    }

    Set<String> imports() {
        Set<String> imports = new HashSet<>();
        //javadoc
        imports.addAll(javadoc.imports());
        //annotations
        annotations.stream().map(AnnotationInstanceSpec::imports).forEach(imports::addAll);
        return imports;
    }

    public static abstract class MemberBuilder<T extends MemberBuilder<T, K>, K extends Javadoc> {
        protected final List<Modifier> modifiers = new ArrayList<>();
        protected final List<AnnotationInstanceSpec> annotations = new ArrayList<>();
        private K javadoc;

        protected MemberBuilder(K javadoc) {
            this.javadoc = argNotNull(javadoc, "javadoc");
        }

        protected abstract void verifyModifier(Modifier modifier);

        public T setJavadoc(K javadoc) {
            this.javadoc = argNotNull(javadoc, "javadoc");
            return (T) this;
        }

        protected abstract Javadoc.AbstractJavadocBuilder<?> getJavadocBuilder();

        public T setJavadoc(String format, Object... args) {
            return setJavadoc((K) getJavadocBuilder().beginJavadoc().addDoc(format, args).end().build());
        }

        public T setJavadoc(JavaCodeBlock codeBlock) {
            return setJavadoc((K) AttributeJavadoc.builder().beginJavadoc().addDoc(codeBlock).end().build());
        }

        public T setNamedJavadoc(String format, Map<String, ?> args) {
            return setJavadoc((K) AttributeJavadoc.builder().beginJavadoc().addNamedDoc(format, args).end().build());
        }

        public T addModifier(Modifier modifier) {
            // must be a legal modifier
            verifyModifier(argNotNull(modifier, "modifier"));

            // remove all potential mutual exclusions
            // TODO: 抛异常
            ModifierSupport.getMutualExclusions(modifier).forEach(this.modifiers::remove);

            this.modifiers.add(modifier);

            return (T) this;
        }

        public T addModifiers(Iterable<Modifier> modifiers) {
            int index = -1;
            for (Modifier modifier : modifiers) {
                addModifier(argElementNotNull(modifier, ++index, "modifiers"));
            }
            return (T) this;
        }

        public T addModifiers(Modifier... modifiers) {
            return addModifiers(Arrays.asList(modifiers));
        }

        public T addAnnotation(AnnotationInstanceSpec annotationSpec) {
            this.annotations.add(annotationSpec);
            return (T) this;
        }

        public T addAnnotation(Class<? extends Annotation> annotationClass) {
            this.annotations.add(AnnotationInstanceSpec.builder(annotationClass).build());
            return (T) this;
        }

        public T addAnnotations(AnnotationInstanceSpec... annotationSpecs) {
            Stream.of(annotationSpecs).forEach(this::addAnnotation);
            return (T) this;
        }

        public T addAnnotations(Iterable<AnnotationInstanceSpec> annotationSpecs) {
            int index = -1;
            for (AnnotationInstanceSpec annotationSpec : argNotNull(annotationSpecs, "annotationSpecs")) {
                addAnnotation(argElementNotNull(annotationSpec, ++index, "annotationSpecs"));
            }
            return (T) this;
        }
    }
}
