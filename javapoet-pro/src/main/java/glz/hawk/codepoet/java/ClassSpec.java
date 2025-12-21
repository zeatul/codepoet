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
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.TypeName;
import glz.hawk.codepoet.java.type.TypeVariableName;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawk.codepoet.java.TypeCategory.CLASS;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ClassSpec extends TypeSpec {
    private ClassSpec(ClassSpec.Builder builder) {
        super(builder);
    }

    public static ClassSpec.Builder builder(String name, Modifier... modifiers) {
        return new ClassSpec.Builder(name, modifiers);
    }

    /**
     * for enum constant
     */
    public static ClassSpec.Builder anonymousBuilder(String format, Object... args) {
        return new ClassSpec.Builder(null, JavaCodeBlock.of(format, args));
    }

    /**
     * for enum constant
     */
    public static ClassSpec.Builder anonymousBuilder(@Nullable JavaCodeBlock codeBlock) {
        return new ClassSpec.Builder(null, codeBlock);
    }

    /**
     * for enum constant and lambda
     */
    public static ClassSpec.Builder anonymousBuilder() {
        return new ClassSpec.Builder((ClassName) null, null);
    }

    /**
     * for anonymous class extends super class with construct arguments.
     */
    public static ClassSpec.Builder anonymousBuilder(ClassName superClass, String format, Object... args) {
        return new ClassSpec.Builder(superClass, JavaCodeBlock.of(format, args));
    }

    /**
     * for anonymous class extends super class with construct arguments.
     */
    public static ClassSpec.Builder anonymousBuilder(ClassName superClass, JavaCodeBlock codeBlock) {
        return new ClassSpec.Builder(argNotNull(superClass, "superClass"), codeBlock);
    }

    /**
     * for anonymous class extends super class with construct arguments.
     */
    public static ClassSpec.Builder anonymousBuilder(Class<?> superClass, JavaCodeBlock codeBlock) {
        return new ClassSpec.Builder(ClassName.ofClass(superClass), codeBlock);
    }

    /**
     * for anonymous class extends super class.
     */
    public static ClassSpec.Builder anonymousBuilder(TypeName superClass) {
        return new ClassSpec.Builder(argNotNull(superClass, "superClass"), null);
    }

    /**
     * for anonymous class extends super class.
     */
    public static ClassSpec.Builder anonymousBuilder(Class<?> superClass) {
        return anonymousBuilder(ClassName.ofClass(superClass));
    }

    @Override
    public boolean isAnonymous() {
        return super.isAnonymous();
    }

    public static class Builder extends AbstractBuilder<ClassSpec.Builder> {
        protected Builder(String name, Modifier... modifiers) {
            super(CLASS, name, modifiers);
        }

        /**
         * for anonymous class
         */
        protected Builder(TypeName superClass, JavaCodeBlock codeBlock) {
            super(superClass, codeBlock);
        }

        @Override
        public ClassSpec.Builder addTypeVariables(Iterable<TypeVariableName> typeVariableNames) {
            return super.addTypeVariables(typeVariableNames);
        }

        @Override
        public ClassSpec.Builder addTypeVariable(TypeVariableName typeVariableName) {
            return super.addTypeVariable(typeVariableName);
        }

        @Override
        public ClassSpec.Builder addTypeVariables(TypeVariableName... typeVariableNames) {
            return super.addTypeVariables(typeVariableNames);
        }

        @Override
        public ClassSpec.Builder setSuperClass(Class<?> clazz) {
            return super.setSuperClass(clazz);
        }

        @Override
        public ClassSpec.Builder setSuperClass(TypeName superClass) {
            return super.setSuperClass(superClass);
        }

        @Override
        public ClassSpec.Builder addSuperInterface(Class<?> superInterface) {
            return super.addSuperInterface(superInterface);
        }

        @Override
        public ClassSpec.Builder addSuperInterface(TypeName superInterface) {
            return super.addSuperInterface(superInterface);
        }

        @Override
        public ClassSpec.Builder addSuperInterfaces(Iterable<TypeName> superInterfaces) {
            return super.addSuperInterfaces(superInterfaces);
        }

        @Override
        public ClassSpec.Builder addSuperInterfaces(TypeName... superInterfaces) {
            return super.addSuperInterfaces(superInterfaces);
        }

        @Override
        public ClassSpec.Builder addSuperInterfacesByClasses(Iterable<Class<?>> superInterfaces) {
            return super.addSuperInterfacesByClasses(superInterfaces);
        }

        @Override
        public ClassSpec.Builder addSuperInterfacesByClasses(Class<?>... superInterfaces) {
            return super.addSuperInterfacesByClasses(superInterfaces);
        }

        @Override
        public ClassSpec.Builder addField(Type type, String name, Modifier... modifiers) {
            return super.addField(type, name, modifiers);
        }

        @Override
        public ClassSpec.Builder addField(TypeName type, String name, Modifier... modifiers) {
            return super.addField(type, name, modifiers);
        }

        @Override
        public ComplexJavaCodeBlockBuilder<ClassSpec.Builder> beginInstanceInitializer() {
            return super.beginInstanceInitializer();
        }

        @Override
        public ComplexJavaCodeBlockBuilder<ClassSpec.Builder> beginStaticInitializer() {
            return super.beginStaticInitializer();
        }

        @Override
        public ClassSpec.Builder addConstructor(ConstructorSpec constructorSpec) {
            return super.addConstructor(constructorSpec);
        }

        @Override
        public ClassSpec.Builder addConstructors(ConstructorSpec... constructorSpecs) {
            return super.addConstructors(constructorSpecs);
        }

        @Override
        public ClassSpec.Builder addConstructors(Iterable<ConstructorSpec> constructorSpecs) {
            return super.addConstructors(constructorSpecs);
        }

        @Override
        public ClassSpec.Builder addMethod(MethodSpec methodSpec) {
            return super.addMethod(methodSpec);
        }

        @Override
        public ClassSpec.Builder addMethods(MethodSpec... methodSpecs) {
            return super.addMethods(methodSpecs);
        }

        @Override
        public ClassSpec.Builder addMethods(Iterable<MethodSpec> methodSpecs) {
            return super.addMethods(methodSpecs);
        }

        public ClassSpec build() {
            return new ClassSpec(this);
        }
    }
}
