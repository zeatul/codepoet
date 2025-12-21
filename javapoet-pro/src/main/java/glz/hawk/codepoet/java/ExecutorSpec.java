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
import glz.hawk.codepoet.java.javadoc.Javadoc;
import glz.hawk.codepoet.java.type.ArrayTypeName;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.TypeName;
import glz.hawk.codepoet.java.type.TypeVariableName;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static glz.hawkframework.core.support.ArgumentSupport.*;
import static glz.hawkframework.core.support.LogicSupport.consumeIfNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
@SuppressWarnings("ALL")
public abstract class ExecutorSpec<K extends Javadoc> extends MemberSpec<K> {
    public final List<TypeVariableName> typeVariables;
    public final List<ParameterSpec> parameters;
    public final List<TypeName> throwables;
    public final JavaCodeBlock methodBodyCode;
    public final TypeName returnType;
    public final String name;
    public final JavaCodeBlock defaultValue;
    public final boolean varargs;

    protected ExecutorSpec(ExecutorBuilder<?, K> builder) {
        super(builder);
        this.typeVariables = Collections.unmodifiableList(builder.typeVariables);
        this.parameters = Collections.unmodifiableList(builder.parameters);
        this.throwables = Collections.unmodifiableList(builder.throwables);
        this.methodBodyCode = builder.methodBodyBuilder.buildCodeBlock();
        this.returnType = builder.returnType;
        this.name = builder.name;
        this.defaultValue = builder.defaultValueBuilder.build();
        this.varargs = builder.varargs;

        if (builder.varargs && !lastParameterIsArray()) {
            throw new IllegalArgumentException(String.format("last parameter of varargs %s must be an array", name != null ? "method: " + name : "Constructor"));
        }

        if (modifiers.contains(Modifier.ABSTRACT) && methodBodyCode.isNotEmpty()) {
            throw new IllegalArgumentException(String.format("Abstract method: %s can't have code.", name));
        }
    }

    private boolean lastParameterIsArray() {
        return !parameters.isEmpty()
            && (parameters.get(parameters.size() - 1).type instanceof ArrayTypeName);
    }

    Set<String> imports() {
        // javadoc
        Set<String> imports = new HashSet<>(javadoc.imports());
        // annotations
        this.annotations.stream().map(AnnotationInstanceSpec::imports).forEach(imports::addAll);
        // typeVariables
        this.typeVariables.stream().map(TypeVariableName::imports).forEach(imports::addAll);
        // parameters
        this.parameters.stream().map(ParameterSpec::imports).forEach(imports::addAll);
        // throwables
        this.throwables.stream().map(TypeName::imports).forEach(imports::addAll);
        // method body code
        imports.addAll(methodBodyCode.imports());
        // return type
        consumeIfNotNull(returnType, r -> imports.addAll(r.imports()));
        // default value
        imports.addAll(defaultValue.imports());
        return imports;
    }


    void emit(JavaCodeWriter codeWriter, String className, boolean hasExecutorBody, Set<Modifier> legalModifiers, Set<Modifier> implicitModifiers) throws IOException {
        //javadoc
        codeWriter.emitJavadoc(javadoc);
        //annotations;
        codeWriter.emitAnnotations(annotations, false);
        //modifiers
        if (!hasExecutorBody && modifiers.contains(Modifier.STRICTFP)) {
            throw new IllegalStateException("The modifier: strictfp is illegal for abstract method.");
        }
        // dynamic implicit modifiers
        Set<Modifier> finalImplicitModifiers = new HashSet<>(implicitModifiers);
        finalImplicitModifiers.addAll(ModifierSupport.dynamicImplicitMethodModifiers(modifiers));
        codeWriter.emitModifiers(modifiers, legalModifiers, finalImplicitModifiers);

        // typeVariables;
        if (!typeVariables.isEmpty()) {
            codeWriter.emitTypeVariables(typeVariables);
            codeWriter.emit(" ");
        }

        // return type and name
        emitReturnTypeAndName(codeWriter, className);

        // parameters
        int index = 0;
        for (ParameterSpec parameterSpec : parameters) {
            parameterSpec.emit(codeWriter, index == parameters.size() - 1 && this.varargs);
            if (index < parameters.size() - 1) {
                codeWriter.emit(", ");
            }
            index++;
        }
        codeWriter.emit(")");

        // default value
        if (defaultValue.isNotEmpty()) {
            codeWriter.emit(" default ").emit(defaultValue).emit(";").emitNewLine();
            return;
        }

        // throwables
        for (int i = 0; i < throwables.size(); i++) {
            if (i == 0) {
                codeWriter.emit(" throws $T", throwables.get(i));
            } else {
                codeWriter.emit(", $T", throwables.get(i));
            }
        }

        if (hasExecutorBody) {
            codeWriter.emit(" {").emitNewLine();
            //code
            codeWriter.indent();
            codeWriter.emit(methodBodyCode);
            codeWriter.unindent();
            //end
            codeWriter.emit("}").emitNewLine();
        } else {
            codeWriter.emit(";").emitNewLine();
        }
    }

    protected abstract void emitReturnTypeAndName(JavaCodeWriter codeWriter, String className) throws IOException;

    protected static abstract class ExecutorBuilder<T extends ExecutorBuilder<T, K>, K extends Javadoc> extends MemberBuilder<T, K> {
        public final List<TypeName> throwables = new ArrayList<>();
        protected final ComplexJavaCodeBlockBuilder<T> methodBodyBuilder = new ComplexJavaCodeBlockBuilder<>(JavaCodeBlock.builder(), (T) this);
        protected final JavaCodeBlock.Builder defaultValueBuilder = JavaCodeBlock.builder();
        private final List<TypeVariableName> typeVariables = new ArrayList<>();
        private final List<ParameterSpec> parameters = new ArrayList<>();
        private final TypeName returnType;
        private final String name;
        private boolean varargs = false;

        protected ExecutorBuilder(K javadoc, TypeName returnType, String name, Modifier... modifiers) {
            super(javadoc);
            this.returnType = returnType;
            this.name = name;
            addModifiers(modifiers);
        }

        public T addTypeVariable(TypeVariableName typeVariableName) {
            this.typeVariables.add(argNotNull(typeVariableName, "typeVariableName"));
            return (T) this;
        }

        public T add(Consumer<T> consumer) {
            consumer.accept((T) this);
            return (T) this;
        }


        public T addTypeVariables(Iterable<TypeVariableName> typeVariableNames) {
            int index = -1;
            for (TypeVariableName typeVariableName : argNotNull(typeVariableNames, "typeVariableNames")) {
                this.typeVariables.add(argElementNotNull(typeVariableName, ++index, "typeVariableNames"));
            }
            return (T) this;
        }

        public T addTypeVariables(TypeVariableName... typeVariableNames) {
            return addTypeVariables(Arrays.asList(typeVariableNames));
        }

        protected T addParameter(TypeName type, String name, Modifier... modifiers) {
            this.parameters.add(ParameterSpec.builder(type, name, modifiers).build());
            return (T) this;
        }

        protected T addParameter(Class<?> clazz, String name, Modifier... modifiers) {
            this.parameters.add(ParameterSpec.builder(clazz, name, modifiers).build());
            return (T) this;
        }

        protected T addParameter(ParameterSpec parameterSpec) {
            this.parameters.add(argNotNull(parameterSpec, "parameterSpec"));
            return (T) this;
        }

        protected T addParameters(Iterable<ParameterSpec> parameterSpecs) {
            for (ParameterSpec parameterSpec : argNotNull(parameterSpecs, "parameterSpecs")) {
                addParameter(parameterSpec);
            }
            return (T) this;
        }

        protected T addParameters(ParameterSpec... parameterSpecs) {
            return addParameters(Arrays.asList(parameterSpecs));
        }

        protected T varargs() {
            this.varargs = true;
            return (T) this;
        }

        protected T addThrowable(TypeName throwableTypeName) {
            this.throwables.add(argNotNull(throwableTypeName, "throwableTypeName"));
            return (T) this;
        }

        protected T addThrowable(Class<? extends Throwable> throwableClass) {
            this.throwables.add(ClassName.ofClass((throwableClass)));
            return (T) this;
        }

        protected T addThrowables(List<TypeName> throwableTypeNames) {
            this.throwables.addAll(argNoNullElement(throwableTypeNames, "throwableTypeNames"));
            return (T) this;
        }

        protected T addThrowables(Iterable<Class<? extends Throwable>> throwableClasses) {
            int index = 0;
            for (Class<? extends Throwable> throwableClass : argNotNull(throwableClasses, "throwableClasses")) {
                this.throwables.add(ClassName.ofClass(argNotNull(throwableClass, String.format("the %dth element in throwableClasses", ++index))));
            }
            return (T) this;
        }

        protected T addThrowables(Class<? extends Throwable>... throwableClasses) {
            return addThrowables(Arrays.asList(throwableClasses));
        }
    }

}
