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

import glz.hawk.codepoet.java.javacode.ComplexJavaCodeBlockBuilder;
import glz.hawk.codepoet.java.javadoc.ConstructorJavadoc;
import glz.hawk.codepoet.java.type.TypeName;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ConstructorSpec extends ExecutorSpec<ConstructorJavadoc> {
    protected ConstructorSpec(Builder builder) {
        super(builder);
    }

    public static Builder builder(Modifier... modifiers) {
        return new Builder(ConstructorJavadoc.builder().build(), modifiers);
    }

    @Override
    protected void emitReturnTypeAndName(JavaCodeWriter codeWriter, String className) throws IOException {
        codeWriter.emit("$L(", className);
    }

    public static final class Builder extends ExecutorBuilder<Builder, ConstructorJavadoc> {
        private Builder(ConstructorJavadoc javadoc, Modifier... modifiers) {
            super(javadoc, null, null, modifiers);
        }

        public Builder setJavadoc(Function<ConstructorJavadoc.Builder, ConstructorJavadoc> function) {
            setJavadoc(function.apply(ConstructorJavadoc.builder()));
            return this;
        }

        @Override
        public Builder addParameters(Iterable<ParameterSpec> parameterSpecs) {
            return super.addParameters(parameterSpecs);
        }

        @Override
        public Builder addParameter(ParameterSpec parameterSpec) {
            return super.addParameter(parameterSpec);
        }

        @Override
        public Builder addParameter(Class<?> clazz, String name, Modifier... modifiers) {
            return super.addParameter(clazz, name, modifiers);
        }

        @Override
        public Builder addParameter(TypeName type, String name, Modifier... modifiers) {
            return super.addParameter(type, name, modifiers);
        }

        @Override
        public Builder addParameters(ParameterSpec... parameterSpecs) {
            return super.addParameters(parameterSpecs);
        }

        @Override
        public Builder addThrowables(Iterable<Class<? extends Throwable>> throwableClasses) {
            return super.addThrowables(throwableClasses);
        }

        @Override
        public Builder varargs() {
            return super.varargs();
        }

        @Override
        public Builder addThrowable(Class<? extends Throwable> throwableClass) {
            return super.addThrowable(throwableClass);
        }

        @Override
        public Builder addThrowable(TypeName throwableTypeName) {
            return super.addThrowable(throwableTypeName);
        }

        @Override
        public Builder addThrowables(Class<? extends Throwable>... throwableClasses) {
            return super.addThrowables(throwableClasses);
        }

        public ComplexJavaCodeBlockBuilder<Builder> beginConstructorBody() {
            return this.methodBodyBuilder;
        }

        @Override
        protected void verifyModifier(Modifier modifier) {
            argument(modifier, MemberCategory.CONSTRUCTOR::isValid, m -> String.format("The modifier: %s is illegal for method", modifier));
        }

        @Override
        protected ConstructorJavadoc.Builder getJavadocBuilder() {
            return ConstructorJavadoc.builder();
        }

        public ConstructorSpec build() {
            return new ConstructorSpec(this);
        }
    }
}

