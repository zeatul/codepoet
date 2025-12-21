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
import glz.hawk.codepoet.java.type.TypeVariableName;

import javax.lang.model.element.Modifier;

import static glz.hawk.codepoet.java.TypeCategory.INTERFACE;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class InterfaceSpec extends TypeSpec {

    private InterfaceSpec(InterfaceSpec.Builder builder) {
        super(builder);
    }

    public static InterfaceSpec.Builder builder(String name, Modifier... modifiers) {
        return new InterfaceSpec.Builder(name, modifiers);
    }

    public static class Builder extends AbstractBuilder<InterfaceSpec.Builder> {
        protected Builder(String name, Modifier... modifiers) {
            super(INTERFACE, name, modifiers);
        }

        @Override
        public InterfaceSpec.Builder addTypeVariables(Iterable<TypeVariableName> typeVariableNames) {
            return super.addTypeVariables(typeVariableNames);
        }

        @Override
        public InterfaceSpec.Builder addTypeVariable(TypeVariableName typeVariableName) {
            return super.addTypeVariable(typeVariableName);
        }

        @Override
        public InterfaceSpec.Builder addTypeVariables(TypeVariableName... typeVariableNames) {
            return super.addTypeVariables(typeVariableNames);
        }

        @Override
        public InterfaceSpec.Builder addSuperInterface(Class<?> superInterface) {
            return super.addSuperInterface(superInterface);
        }

        @Override
        public InterfaceSpec.Builder addSuperInterface(TypeName superInterface) {
            return super.addSuperInterface(superInterface);
        }

        @Override
        public InterfaceSpec.Builder addSuperInterfaces(Iterable<TypeName> superInterfaces) {
            return super.addSuperInterfaces(superInterfaces);
        }

        @Override
        public InterfaceSpec.Builder addSuperInterfaces(TypeName... superInterfaces) {
            return super.addSuperInterfaces(superInterfaces);
        }

        @Override
        public InterfaceSpec.Builder addSuperInterfacesByClasses(Iterable<Class<?>> superInterfaces) {
            return super.addSuperInterfacesByClasses(superInterfaces);
        }

        @Override
        public InterfaceSpec.Builder addSuperInterfacesByClasses(Class<?>... superInterfaces) {
            return super.addSuperInterfacesByClasses(superInterfaces);
        }

        @Override
        public InterfaceSpec.Builder addMethod(MethodSpec methodSpec) {
            return super.addMethod(methodSpec);
        }

        @Override
        public InterfaceSpec.Builder addMethods(MethodSpec... methodSpecs) {
            return super.addMethods(methodSpecs);
        }

        @Override
        public InterfaceSpec.Builder addMethods(Iterable<MethodSpec> methodSpecs) {
            return super.addMethods(methodSpecs);
        }

        public InterfaceSpec build() {
            return new InterfaceSpec(this);
        }
    }
}
