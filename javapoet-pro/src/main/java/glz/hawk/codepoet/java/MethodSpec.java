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
import glz.hawk.codepoet.java.javadoc.MethodJavadoc;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.TypeName;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class MethodSpec extends ExecutorSpec<MethodJavadoc> {

    protected MethodSpec(Builder builder) {
        super(builder);
    }

    public static Builder builder(TypeName returnType, String name, Modifier... modifiers) {
        return new Builder(returnType, name, modifiers);
    }

    public static Builder builder(Class<?> returnType, String name, Modifier... modifiers) {
        return new Builder(ClassName.ofClass(returnType), name, modifiers);
    }


    @Override
    protected void emitReturnTypeAndName(JavaCodeWriter codeWriter, String className) throws IOException {
        argFalse(() -> name.equals(className), () -> "The method name can't be same as class name.");
        codeWriter.emit("$T $L(", returnType, name);
    }

    public static final class Builder extends ExecutorBuilder<Builder, MethodJavadoc> {
        private Builder(TypeName returnType, String name, Modifier... modifiers) {
            super(MethodJavadoc.builder().build(), returnType, name, modifiers);
            //TODO:校验name
            //TODO:如何保证名称不和类名相同
            argNotBlank(name, "name");
            //TODO:returnType的默认值 ,java.lang.Void is illegal
            argNotNull(returnType, "returnType");
        }

        public Builder setJavadoc(Function<MethodJavadoc.Builder, MethodJavadoc> function) {
            setJavadoc(function.apply(MethodJavadoc.builder()));
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
        public Builder varargs() {
            return super.varargs();
        }

        @Override
        public Builder addThrowables(Iterable<Class<? extends Throwable>> throwableClasses) {
            return super.addThrowables(throwableClasses);
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
        public Builder addThrowables(List<TypeName> throwableTypeNames) {
            return super.addThrowables(throwableTypeNames);
        }

        @Override
        public Builder addThrowables(Class<? extends Throwable>... throwableClasses) {
            return super.addThrowables(throwableClasses);
        }

        @Override
        protected void verifyModifier(Modifier modifier) {
            argument(modifier, MemberCategory.METHOD::isValid, m -> String.format("The modifier: %s is illegal for method", modifier));
        }

        @Override
        protected MethodJavadoc.Builder getJavadocBuilder() {
            return MethodJavadoc.builder();
        }

        public ComplexJavaCodeBlockBuilder<Builder> beginMethodBody() {
            return this.methodBodyBuilder;
        }

        public MethodSpec build() {
            return new MethodSpec(this);
        }
    }
}
