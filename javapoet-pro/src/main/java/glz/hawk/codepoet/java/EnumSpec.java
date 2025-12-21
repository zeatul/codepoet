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
import glz.hawk.codepoet.java.type.TypeName;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawk.codepoet.java.TypeCategory.ENUM;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class EnumSpec extends TypeSpec {
    private EnumSpec(EnumSpec.Builder builder) {
        super(builder);
    }

    public static EnumSpec.Builder builder(String name, Modifier... modifiers) {
        return new EnumSpec.Builder(name, modifiers);
    }

    public static class Builder extends AbstractBuilder<EnumSpec.Builder> {
        protected Builder(String name, Modifier... modifiers) {
            super(ENUM, name, modifiers);
        }

        public static EnumSpec.Builder builder(String name) {
            return new EnumSpec.Builder(name);
        }

        @Override
        public EnumSpec.Builder addSuperInterface(Class<?> superInterface) {
            return super.addSuperInterface(superInterface);
        }

        @Override
        public EnumSpec.Builder addSuperInterface(TypeName superInterface) {
            return super.addSuperInterface(superInterface);
        }

        @Override
        public EnumSpec.Builder addSuperInterfaces(Iterable<TypeName> superInterfaces) {
            return super.addSuperInterfaces(superInterfaces);
        }

        @Override
        public EnumSpec.Builder addSuperInterfaces(TypeName... superInterfaces) {
            return super.addSuperInterfaces(superInterfaces);
        }

        @Override
        public EnumSpec.Builder addSuperInterfacesByClasses(Iterable<Class<?>> superInterfaces) {
            return super.addSuperInterfacesByClasses(superInterfaces);
        }

        @Override
        public EnumSpec.Builder addSuperInterfacesByClasses(Class<?>... superInterfaces) {
            return super.addSuperInterfacesByClasses(superInterfaces);
        }

        public EnumSpec.Builder addEnumConstant(String name) {
            return addEnumConstant(name, ClassSpec.anonymousBuilder().build());
        }

        public EnumSpec.Builder addEnumConstant(String name, String format, Object... args) {
            return addEnumConstant(name, JavaCodeBlock.of(format, args));
        }

        public EnumSpec.Builder addEnumConstant(String name, JavaCodeBlock codeBlock) {
            return addEnumConstant(name, ClassSpec.anonymousBuilder(codeBlock).build());
        }

        public EnumSpec.Builder addEnumConstant(String name, TypeSpec typeSpec) {
            if (this.enumConstants.put(argNotBlank(name, "name"), argNotNull(typeSpec, "typeSpec")) != null) {
                throw new IllegalArgumentException(String.format("The name: %s is used by other enum constant before.", name));
            }
            return this;
        }

        @Override
        public EnumSpec.Builder addField(Type type, String name, Modifier... modifiers) {
            return super.addField(type, name, modifiers);
        }

        @Override
        public EnumSpec.Builder addField(TypeName type, String name, Modifier... modifiers) {
            return super.addField(type, name, modifiers);
        }

        @Override
        public ComplexJavaCodeBlockBuilder<EnumSpec.Builder> beginStaticInitializer() {
            return super.beginStaticInitializer();
        }

        @Override
        public ComplexJavaCodeBlockBuilder<EnumSpec.Builder> beginInstanceInitializer() {
            return super.beginInstanceInitializer();
        }

        @Override
        public EnumSpec.Builder addConstructor(ConstructorSpec constructorSpec) {
            return super.addConstructor(constructorSpec);
        }

        @Override
        public EnumSpec.Builder addConstructors(ConstructorSpec... constructorSpecs) {
            return super.addConstructors(constructorSpecs);
        }

        @Override
        public EnumSpec.Builder addConstructors(Iterable<ConstructorSpec> constructorSpecs) {
            return super.addConstructors(constructorSpecs);
        }

        @Override
        public EnumSpec.Builder addMethod(MethodSpec methodSpec) {
            return super.addMethod(methodSpec);
        }

        @Override
        public EnumSpec.Builder addMethods(MethodSpec... methodSpecs) {
            return super.addMethods(methodSpecs);
        }

        @Override
        public EnumSpec.Builder addMethods(Iterable<MethodSpec> methodSpecs) {
            return super.addMethods(methodSpecs);
        }

        public EnumSpec build() {
            return new EnumSpec(this);
        }
    }

}
