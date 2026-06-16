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
import glz.hawk.codepoet.java.javadoc.TypeJavadoc;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.TypeName;
import glz.hawk.codepoet.java.type.TypeVariableName;
import glz.hawkframework.core.helper.ObjectHelper;
import glz.hawkframework.core.helper.StringHelper;
import glz.hawkframework.core.support.StateSupport;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

import static glz.hawk.codepoet.java.TypeCategory.*;
import static glz.hawkframework.core.support.ArgumentSupport.*;
import static glz.hawkframework.core.support.StateSupport.stateFalse;
import static glz.hawkframework.core.support.StateSupport.stateTrue;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class TypeSpec {
    public final TypeJavadoc javadoc;
    public final TypeCategory typeCategory;
    public final String name;
    public final List<TypeVariableName> typeVariables;
    public final List<TypeName> superInterfaces;
    public final TypeName superClass;
    public final List<AnnotationInstanceSpec> annotations;
    public final List<FieldSpec> fields;
    public final List<ConstructorSpec> constructors;
    public final List<MethodSpec> methods;
    public final List<AttributeSpec> attributes;
    public final JavaCodeBlock staticInitializer;
    public final JavaCodeBlock instanceInitializer;
    public final JavaCodeBlock anonymousTypeArguments;
    public final Map<String, TypeSpec> enumConstants;
    public final List<TypeSpec> innerTypes;
    private final Set<String> staticImports;
    public Set<Modifier> modifiers;
    public final List<Element> originatingElements;

    protected TypeSpec(AbstractBuilder<?> builder) {
        this.javadoc = builder.javadoc;
        this.typeCategory = builder.typeCategory;
        this.name = builder.name;
        this.typeVariables = Collections.unmodifiableList(builder.typeVariables);
        this.modifiers = Collections.unmodifiableSet(new LinkedHashSet<>(builder.modifiers));
        this.annotations = Collections.unmodifiableList(builder.annotations);
        this.fields = Collections.unmodifiableList(builder.fields);
        this.constructors = Collections.unmodifiableList(builder.constructors);
        this.methods = Collections.unmodifiableList(builder.methods);
        this.attributes = Collections.unmodifiableList(builder.attributes);
        this.superClass = builder.superClass;
        this.superInterfaces = builder.superInterfaces;
        this.staticInitializer = builder.staticInitializerBuilder.buildCodeBlock();
        this.instanceInitializer = builder.instanceInitializerBuilder.buildCodeBlock();
        this.anonymousTypeArguments = builder.anonymousTypeArgumentsBuilder.build();
        this.enumConstants = Collections.unmodifiableMap(builder.enumConstants);
        this.innerTypes = Collections.unmodifiableList(builder.innerTypes);
        this.staticImports = Collections.unmodifiableSet(builder.staticImports);
        this.originatingElements = builder.originatingElements;
    }

    private void addAsMemberModifiers(Modifier... modifiers) {
        HashSet<Modifier> tempModifiers = new HashSet<>(this.modifiers);
        tempModifiers.addAll(Arrays.asList(modifiers));
        this.modifiers = Collections.unmodifiableSet(tempModifiers);
    }

    private void addAsMemberModifiers(Iterable<Modifier> modifiers) {
        HashSet<Modifier> tempModifiers = new HashSet<>(this.modifiers);
        modifiers.forEach(tempModifiers::add);
        this.modifiers = Collections.unmodifiableSet(tempModifiers);
    }

    protected boolean isAnonymous() {
        return StringHelper.isBlank(this.name);
    }

    Set<String> staticImports() {
        Set<String> set = new HashSet<>(this.staticImports);
        this.innerTypes.stream().map(TypeSpec::staticImports).forEach(set::addAll);
        return set;
    }

    Set<String> imports() {
        // TODO: add other imports

        // javadoc
        Set<String> imports = new HashSet<>(javadoc.imports());

        // type variables
        typeVariables.stream().map(TypeVariableName::imports).forEach(imports::addAll);

        // superClass
        if (superClass != null) {
            imports.addAll(superClass.imports());
        }

        // superInterfaces
        superInterfaces.stream().map(TypeName::imports).forEach(imports::addAll);

        // annotations
        annotations.stream().map(AnnotationInstanceSpec::imports).forEach(imports::addAll);

        // enum constant
        this.enumConstants.values().stream().map(TypeSpec::imports).forEach(imports::addAll);

        // fields
        fields.stream().map(FieldSpec::imports).forEach(imports::addAll);

        // static initializer
        imports.addAll(staticInitializer.imports());

        // instance initializer
        imports.addAll(instanceInitializer.imports());

        // constructors
        constructors.stream().map(ConstructorSpec::imports).forEach(imports::addAll);

        // methods
        methods.stream().map(MethodSpec::imports).forEach(imports::addAll);

        // attributes
        attributes.stream().map(AttributeSpec::imports).forEach(imports::addAll);

        // inner types
        this.innerTypes.stream().map(TypeSpec::imports).forEach(imports::addAll);

        return imports;
    }

    void emit(JavaCodeWriter codeWriter, String enumName, boolean isInnerType) throws IOException {
        //emit javadoc
        codeWriter.emitJavadoc(javadoc);

        //emit annotations
        codeWriter.emitAnnotations(annotations, false);

        // emit modifiers
        if (!isInnerType) {
            codeWriter.emitModifiers(modifiers, typeCategory.legalTypeModifiers, typeCategory.implicitTypeModifiers);
        } else {
            HashSet<Modifier> legalTypeModifiers = new HashSet<>(typeCategory.legalTypeModifiers);
            legalTypeModifiers.addAll(typeCategory.legalAsMemberModifiers);
            HashSet<Modifier> implicitTypeModifiers = new HashSet<>(typeCategory.implicitTypeModifiers);
            legalTypeModifiers.addAll(typeCategory.implicitAsMemberModifiers);
            codeWriter.emitModifiers(modifiers, legalTypeModifiers, implicitTypeModifiers);
        }

        if (!isAnonymous()) {
            // emit typeCategory
            codeWriter.emitTypeCategory(typeCategory);
            // emit name
            codeWriter.emitAndIndent(name);
        } else {
            if (StringHelper.isNotBlank(enumName)) {
                codeWriter.emit(enumName);
            } else {
                if (superClass == null) {
                    throw new IllegalArgumentException("The superclass of anonymous class must not be null.");
                }
                codeWriter.emit("new $T", superClass);
            }

            if (anonymousTypeArguments.isNotEmpty()) {
                codeWriter.emit("(").emit(anonymousTypeArguments).emit(")");
            } else {
                if (StringHelper.isBlank(enumName)) {
                    codeWriter.emit("()");
                }
            }
        }

        if (StringHelper.isBlank(enumName)) {
            // emit type variables
            codeWriter.emitTypeVariables(typeVariables);
        }

        if (!isAnonymous()) {
            // emit superClass
            codeWriter.emitSuperClass(superClass);
            // emit superInterfaces
            codeWriter.emitSuperInterfaces(superInterfaces, typeCategory == INTERFACE);
        }

        if (StringHelper.isNotBlank(enumName)) {
            if (this.methods.isEmpty() && this.fields.isEmpty() && this.staticInitializer.isEmpty() && this.instanceInitializer.isEmpty()) {
                return;
            }
        }

        codeWriter.emitAndIndent(" {").emitNewLine();
        codeWriter.indent();

        //this flag means current line is not the fire line of class.
        boolean isFirstLine = true;

        // emit enum constants
        if (typeCategory == ENUM) {
            for (Iterator<Map.Entry<String, TypeSpec>> iterator = this.enumConstants.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, TypeSpec> entry = iterator.next();
                entry.getValue().emit(codeWriter, entry.getKey(), false);
                if (iterator.hasNext()) {
                    codeWriter.emit(",");
                } else {
                    if (ObjectHelper.isNotEmpty(fields) || ObjectHelper.isNotEmpty(methods) || ObjectHelper.isNotEmpty(constructors) ||
                        ObjectHelper.isNotEmpty(innerTypes) || instanceInitializer.isNotEmpty() || staticInitializer.isNotEmpty()) {
                        codeWriter.emit(";");
                    }
                }
                codeWriter.emitNewLine();
            }
            isFirstLine = false;
        }

        // emit fields
        if (!isFirstLine) {
            if (!this.fields.isEmpty()) {
                codeWriter.emitNewLine();
            }
        }
        for (FieldSpec fieldSpec : fields) {
            Set<Modifier> implicitFieldModifiers = new HashSet<>(typeCategory.implicitFieldModifiers);
            if (this.modifiers.contains(Modifier.FINAL)) {
                implicitFieldModifiers.add(Modifier.PROTECTED);
            }
            // emit field on every other line.
            codeWriter.emitNewLine();
            fieldSpec.emit(codeWriter, typeCategory.legalFieldModifiers, implicitFieldModifiers);
            isFirstLine = false;
        }

        // emit static initializer
        if (staticInitializer.isNotEmpty()) {
            codeWriter.emitNewLine();
            codeWriter.emit("static {").emitNewLine();
            //code
            codeWriter.indent();
            codeWriter.emit(staticInitializer);
            codeWriter.unindent();
            //end
            codeWriter.emit("}").emitNewLine();
            isFirstLine = false;
        }

        // emit instance initializer
        if (instanceInitializer.isNotEmpty()) {
            codeWriter.emitNewLine();
            codeWriter.emit("{").emitNewLine();
            //code
            codeWriter.indent();
            codeWriter.emit(instanceInitializer);
            codeWriter.unindent();
            //end
            codeWriter.emit("}").emitNewLine();
            isFirstLine = false;
        }

        // emit constructors
        for (ConstructorSpec constructorSpec : constructors) {
            Set<Modifier> implicitConstructorModifiers = new HashSet<>(typeCategory.implicitConstructorModifiers);
            if (this.modifiers.contains(Modifier.FINAL)) {
                implicitConstructorModifiers.add(Modifier.PROTECTED);
            }
            if (!isFirstLine) {
                codeWriter.emitNewLine();
            }
            constructorSpec.emit(codeWriter, name, true, typeCategory.legalConstructorModifiers, implicitConstructorModifiers);
            isFirstLine = false;
        }

        // emit attributes
        for (AttributeSpec attributeSpec : attributes) {
            if (!isFirstLine) {
                codeWriter.emitNewLine();
            }
            boolean hasExecutorBody = false;
            Set<Modifier> implicitMethodModifiers = new HashSet<>(typeCategory.implicitAttributeModifiers);
            attributeSpec.emit(codeWriter, name, hasExecutorBody, typeCategory.legalMethodModifiers, implicitMethodModifiers);
            isFirstLine = false;
        }

        // emit methods
        for (MethodSpec methodSpec : methods) {
            if (!isFirstLine) {
                codeWriter.emitNewLine();
            }
            boolean hasExecutorBody = true;
            switch (typeCategory) {
                case INTERFACE:
                    if (!methodSpec.modifiers.contains(Modifier.DEFAULT)) {
                        hasExecutorBody = false;
                    }
                    break;
                case ENUM:
                    if (methodSpec.modifiers.contains(Modifier.ABSTRACT)) {
                        hasExecutorBody = false;
                    }
                    break;
                case ANNOTATION:
                    hasExecutorBody = false;
                    break;
                case CLASS:
                    if (modifiers.contains(Modifier.ABSTRACT)) {
                        if (methodSpec.modifiers.contains(Modifier.ABSTRACT)) {
                            hasExecutorBody = false;
                        }
                    } else {
                        if (methodSpec.modifiers.contains(Modifier.ABSTRACT)) {
                            throw new IllegalStateException(String.format("Class: %s is not abstract, but method: %s is abstract", this, methodSpec));
                        }
                    }
                    break;
                default:
                    // Do Nothing
            }
            Set<Modifier> implicitMethodModifiers = new HashSet<>(typeCategory.implicitMethodModifiers);
            if (this.modifiers.contains(Modifier.FINAL)) {
                implicitMethodModifiers.add(Modifier.PROTECTED);
            }
            methodSpec.emit(codeWriter, name, hasExecutorBody, typeCategory.legalMethodModifiers, implicitMethodModifiers);
            isFirstLine = false;
        }

        //emit inner
        for (TypeSpec innerType : this.innerTypes) {
            if (!isFirstLine) {
                codeWriter.emitNewLine();
            }
            innerType.emit(codeWriter, null, true);
            codeWriter.emitNewLine();
            isFirstLine = false;
        }

        codeWriter.unindent();
        codeWriter.emit("}");


    }

    @SuppressWarnings("unchecked")
    protected static abstract class AbstractBuilder<T extends AbstractBuilder<T>> {
        private final Set<Modifier> modifiers = new HashSet<>();
        private final Set<String> staticImports = new LinkedHashSet<>();
        private final List<AnnotationInstanceSpec> annotations = new ArrayList<>();
        private final List<FieldSpec> fields = new ArrayList<>();
        private final List<ConstructorSpec> constructors = new ArrayList<>();
        private final List<MethodSpec> methods = new ArrayList<>();
        private final List<TypeName> superInterfaces = new ArrayList<>();
        private final List<TypeVariableName> typeVariables = new ArrayList<>();
        private final List<TypeSpec> innerTypes = new ArrayList<>();
        private final List<Element> originatingElements = new ArrayList<>();
        protected final Map<String, TypeSpec> enumConstants = new LinkedHashMap<>();
        protected final List<AttributeSpec> attributes = new ArrayList<>();
        private final TypeCategory typeCategory;
        private final String name;
        private final ComplexJavaCodeBlockBuilder<T> staticInitializerBuilder = new ComplexJavaCodeBlockBuilder<>(JavaCodeBlock.builder(), (T) this);
        private final ComplexJavaCodeBlockBuilder<T> instanceInitializerBuilder = new ComplexJavaCodeBlockBuilder<>(JavaCodeBlock.builder(), (T) this);
        private final JavaCodeBlock.Builder anonymousTypeArgumentsBuilder = JavaCodeBlock.builder();
        private TypeJavadoc javadoc = TypeJavadoc.builder().build();
        private TypeName superClass;

        protected AbstractBuilder(TypeCategory typeCategory, String name, Modifier... modifiers) {
            this.typeCategory = argNotNull(typeCategory, "typeCategory");
            //TODO:校验name
            this.name = argNotBlank(name, "name");
            addModifiers(modifiers);
        }

        /**
         * for anonymous class
         */
        protected AbstractBuilder(TypeName superClass, JavaCodeBlock codeBlock) {
            this.typeCategory = CLASS;
            this.name = null;
            this.superClass = superClass;
            if (codeBlock != null) {
                this.anonymousTypeArgumentsBuilder.add(codeBlock);
            }
        }

        public T setJavadoc(TypeJavadoc javadoc) {
            this.javadoc = argNotNull(javadoc, "javadoc");
            return (T) this;
        }

        public T setJavadoc(String format, Object... args) {
            return setJavadoc(TypeJavadoc.builder().beginJavadoc().addDoc(format, args).end().build());
        }

        public T setJavadoc(JavaCodeBlock codeBlock) {
            return setJavadoc(TypeJavadoc.builder().beginJavadoc().addDoc(codeBlock).end().build());
        }

        public T setNamedJavadoc(String format, Map<String, ?> args) {
            return setJavadoc(TypeJavadoc.builder().beginJavadoc().addNamedDoc(format, args).end().build());
        }

        public T setJavadoc(Function<TypeJavadoc.Builder, TypeJavadoc> function) {
            setJavadoc(function.apply(TypeJavadoc.builder()));
            return (T) this;
        }

        private boolean isAnonymous() {
            return StringHelper.isBlank(this.name);
        }

        public T addElement(Element element) {
            this.originatingElements.add(argNotNull(element, "element"));
            return (T) this;
        }

        public T addElement(Element... elements) {
            addElement(Arrays.asList(elements));
            return (T) this;
        }

        public T addElement(Iterable<Element> elements) {
            int index = -1;
            for (Element element : argNotNull(elements, "elements")) {
                this.originatingElements.add(argElementNotNull(element, ++index, "elements"));
            }
            return (T) this;
        }

        public T addAnnotation(AnnotationInstanceSpec annotationSpec) {
            if (isAnonymous() && this.superClass != null) {
                throw new UnsupportedOperationException("Anonymous class cant' have any annotations.");
            }
            this.annotations.add(argNotNull(annotationSpec, "annotationSpec"));
            return (T) this;
        }

        public T addAnnotations(Iterable<AnnotationInstanceSpec> annotationSpecs) {
            int index = -1;
            for (AnnotationInstanceSpec annotationSpec : argNotNull(annotationSpecs, "annotationSpecs")) {
                this.annotations.add(argElementNotNull(annotationSpec, ++index, "annotationSpecs"));
            }
            return (T) this;
        }

        public T addAnnotations(AnnotationInstanceSpec... annotationSpecs) {
            return addAnnotations(Arrays.asList(annotationSpecs));
        }

        protected T addTypeVariable(TypeVariableName typeVariableName) {
            StateSupport.stateFalse(StringHelper.isBlank(this.name) && this.superClass == null, () -> "The anonymous class for enum constant can't have any type variable.");
            this.typeVariables.add(argNotNull(typeVariableName, "typeVariableName"));
            return (T) this;
        }

        protected T addTypeVariables(Iterable<TypeVariableName> typeVariableNames) {
            int index = -1;
            for (TypeVariableName typeVariableName : argNotNull(typeVariableNames, "typeVariableNames")) {
                this.typeVariables.add(argElementNotNull(typeVariableName, ++index, "typeVariableNames"));
            }
            return (T) this;
        }

        protected T addTypeVariables(TypeVariableName... typeVariableNames) {
            return addTypeVariables(Arrays.asList(typeVariableNames));
        }

        public T addModifier(Modifier modifier) {
            // must be a legal modifier
            argument(modifier, typeCategory::isValidForType, m -> String.format("The modifier: %s is illegal for type: %s", modifier, typeCategory));

            // must not be anonymous class
            stateTrue(StringHelper.isNotBlank(this.name), () -> "Anonymous class can't have any modifier.");

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

        public T addField(FieldSpec fieldSpec) {
            //TODO:校验,interface and annotation must have default value
            this.fields.add(argNotNull(fieldSpec, "fieldSpec"));
            return (T) this;
        }

        protected T addField(TypeName type, String name, Modifier... modifiers) {
            return addField(FieldSpec.builder(type, name, modifiers).build());
        }

        protected T addField(Type type, String name, Modifier... modifiers) {
            return addField(FieldSpec.builder(type, name, modifiers).build());
        }

        public T addFields(Iterable<FieldSpec> fieldSpecs) {
            int index = -1;
            for (FieldSpec fieldSpec : argNotNull(fieldSpecs, "fieldSpecs")) {
                addField(argElementNotNull(fieldSpec, ++index, "fieldSpecs"));
            }
            return (T) this;
        }

        public T addFields(FieldSpec... fieldSpecs) {
            return addFields(Arrays.asList(fieldSpecs));
        }

        protected T addConstructor(ConstructorSpec constructorSpec) {
            stateFalse(typeCategory == INTERFACE || typeCategory == ANNOTATION, () -> String.format("The type: %s can't have any constructor.", typeCategory));
            stateFalse(isAnonymous(), () -> "Anonymous class has no constructors.");
            this.constructors.add(argNotNull(constructorSpec, "constructorSpec"));
            return (T) this;
        }

        protected T addConstructors(Iterable<ConstructorSpec> constructorSpecs) {
            int index = -1;
            for (ConstructorSpec constructorSpec : argNotNull(constructorSpecs, "constructorSpecs")) {
                this.constructors.add(argElementNotNull(constructorSpec, ++index, "constructorSpecs"));
            }
            return (T) this;
        }

        protected T addConstructors(ConstructorSpec... constructorSpecs) {
            return addConstructors(Arrays.asList(constructorSpecs));
        }

        protected T addMethod(MethodSpec methodSpec) {
            this.methods.add(argNotNull(methodSpec, "methodSpec"));
            return (T) this;
        }

        protected T addMethods(Iterable<MethodSpec> methodSpecs) {
            int index = -1;
            for (MethodSpec methodSpec : argNotNull(methodSpecs, "methodSpecs")) {
                this.methods.add(argElementNotNull(methodSpec, ++index, "methodSpecs"));
            }
            return (T) this;
        }

        protected T addMethods(MethodSpec... methodSpecs) {
            return addMethods(Arrays.asList(methodSpecs));
        }

        protected T addSuperInterface(TypeName superInterface) {
            //TODO: 校验，必须是interface和支持superInterface
            stateTrue(typeCategory == CLASS || typeCategory == INTERFACE || typeCategory == ENUM, () -> String.format("The type: %s can't have super class.", typeCategory));
            stateFalse(isAnonymous(), () -> "Can't add superInterface to anonymous class.");
            this.superInterfaces.add(argNotNull(superInterface, "superInterface"));
            return (T) this;
        }

        protected T addSuperInterfaces(Iterable<TypeName> superInterfaces) {
            int index = -1;
            for (TypeName superInterface : argNotNull(superInterfaces, "superInterfaces")) {
                addSuperInterface(argElementNotNull(superInterface, ++index, "superInterfaces"));
            }
            return (T) this;
        }

        protected T addSuperInterfaces(TypeName... superInterfaces) {
            return addSuperInterfaces(Arrays.asList(superInterfaces));
        }

        protected T addSuperInterface(Class<?> superInterface) {
            //TODO:校验是否是interface
            return addSuperInterface(ClassName.ofClass(superInterface));
        }

        protected T addSuperInterfacesByClasses(Iterable<Class<?>> superInterfaces) {
            int index = -1;
            for (Class<?> superInterface : superInterfaces) {
                addSuperInterface(argElementNotNull(superInterface, ++index, "superInterfaces"));
            }
            return (T) this;
        }

        protected T addSuperInterfacesByClasses(Class<?>... superInterfaces) {
            return addSuperInterfacesByClasses(Arrays.asList(superInterfaces));
        }


        protected T setSuperClass(TypeName superClass) {
            stateTrue(typeCategory == CLASS, () -> String.format("The type: %s can't have super class.", typeCategory));
//            stateFalse(isAnonymous(), () -> "Can't set the superClass of anonymous class.");
            this.superClass = argNotNull(superClass, "superClass");
            return (T) this;
        }

        protected T setSuperClass(Class<?> clazz) {
            //TODO:校验是否是非final的class
            return setSuperClass(ClassName.ofClass(clazz));
        }

        protected ComplexJavaCodeBlockBuilder<T> beginStaticInitializer() {
            //TODO: Util Java 16, Inner Class Start to Support static initializer
            return this.staticInitializerBuilder;
        }

        protected ComplexJavaCodeBlockBuilder<T> beginInstanceInitializer() {
            //TODO: Util Java 16, Inner Class Start to Support static initializer
            return this.instanceInitializerBuilder;
        }

        public T addInnerType(TypeSpec innerType, Modifier... modifiers) {
            return addInnerType(innerType, Arrays.asList(modifiers));
        }

        public T addInnerType(TypeSpec innerType, Iterable<Modifier> modifiers) {
            argNotNull(innerType, "innerType");
            int index = -1;
            List<Modifier> list = new ArrayList<>();
            for (Modifier modifier : argNotNull(modifiers, "modifiers")) {
                argElementNotNull(modifier, ++index, "modifiers");
                argument(modifier, typeCategory::isValidForAsMember, m -> String.format("The modifier: %s is illegal for inner type: %s", modifier, typeCategory));
                list.add(modifier);
            }
            innerType.addAsMemberModifiers(list);
            this.innerTypes.add(innerType);
            return (T) this;
        }

        public T addStaticImport(String... staticImports) {
            for (String staticImport : staticImports) {
                if (StringHelper.isBlank(staticImport)) throw new IllegalArgumentException("staticImport must not be blank");
                this.staticImports.add(staticImport);
            }
            return (T) this;
        }

        public T addStaticImport(Enum<?> constant) {
            return addStaticImport(ClassName.ofClass(constant.getDeclaringClass()), argNotNull(constant, "constant").name());
        }

        public T addStaticImport(Class<?> clazz, String... names) {
            return addStaticImport(ClassName.ofClass(clazz), names);
        }

        public T addStaticImport(ClassName className, String... names) {
            argNotNull(className, "className");
            int index = -1;
            for (String name : names) {
                argElementNotBlank(name, ++index, "names");
                this.staticImports.add(className.canonicalName() + "." + name);
            }
            return (T) this;
        }

    }

//    public static class ClassSpec extends TypeSpec {
//        private ClassSpec(Builder builder) {
//            super(builder);
//        }
//
//        public static Builder builder(String name, Modifier... modifiers) {
//            return new Builder(name, modifiers);
//        }
//
//        /**
//         * for enum constant
//         */
//        public static Builder anonymousBuilder(String format, Object... args) {
//            return new Builder(null, JavaCodeBlock.of(format, args));
//        }
//
//        /**
//         * for enum constant
//         */
//        public static Builder anonymousBuilder(@Nullable JavaCodeBlock codeBlock) {
//            return new Builder(null, codeBlock);
//        }
//
//        /**
//         * for enum constant and lambda
//         */
//        public static Builder anonymousBuilder() {
//            return new Builder((ClassName) null,  null);
//        }
//
//        /**
//         * for anonymous class extends super class with construct arguments.
//         */
//        public static Builder anonymousBuilder(ClassName superClass, String format, Object... args) {
//            return new Builder(superClass, JavaCodeBlock.of(format, args));
//        }
//
//        /**
//         * for anonymous class extends super class with construct arguments.
//         */
//        public static Builder anonymousBuilder(ClassName superClass, JavaCodeBlock codeBlock) {
//            return new Builder(argNotNull(superClass, "superClass"), codeBlock);
//        }
//
//        /**
//         * for anonymous class extends super class with construct arguments.
//         */
//        public static Builder anonymousBuilder(Class<?> superClass, JavaCodeBlock codeBlock) {
//            return new Builder(ClassName.ofClass(superClass), codeBlock);
//        }
//
//        /**
//         * for anonymous class extends super class.
//         */
//        public static Builder anonymousBuilder(ClassName superClass) {
//            return new Builder(argNotNull(superClass, "superClass"), null);
//        }
//
//        /**
//         * for anonymous class extends super class.
//         */
//        public static Builder anonymousBuilder(Class<?> superClass) {
//            return anonymousBuilder(ClassName.ofClass(superClass));
//        }
//
//        @Override
//        public boolean isAnonymous() {
//            return super.isAnonymous();
//        }
//
//        public static class Builder extends AbstractBuilder<Builder> {
//            protected Builder(String name, Modifier... modifiers) {
//                super(CLASS, name, modifiers);
//            }
//
//            /**
//             * for anonymous class
//             */
//            protected Builder(ClassName superClass, JavaCodeBlock codeBlock) {
//                super(superClass, codeBlock);
//            }
//
//            @Override
//            public Builder addTypeVariables(Iterable<TypeVariableName> typeVariableNames) {
//                return super.addTypeVariables(typeVariableNames);
//            }
//
//            @Override
//            public Builder addTypeVariable(TypeVariableName typeVariableName) {
//                return super.addTypeVariable(typeVariableName);
//            }
//
//            @Override
//            public Builder addTypeVariables(TypeVariableName... typeVariableNames) {
//                return super.addTypeVariables(typeVariableNames);
//            }
//
//            @Override
//            public Builder setSuperClass(Class<?> clazz) {
//                return super.setSuperClass(clazz);
//            }
//
//            @Override
//            public Builder setSuperClass(TypeName superClass) {
//                return super.setSuperClass(superClass);
//            }
//
//            @Override
//            public Builder addSuperInterface(Class<?> superInterface) {
//                return super.addSuperInterface(superInterface);
//            }
//
//            @Override
//            public Builder addSuperInterface(TypeName superInterface) {
//                return super.addSuperInterface(superInterface);
//            }
//
//            @Override
//            public Builder addSuperInterfaces(Iterable<TypeName> superInterfaces) {
//                return super.addSuperInterfaces(superInterfaces);
//            }
//
//            @Override
//            public Builder addSuperInterfaces(TypeName... superInterfaces) {
//                return super.addSuperInterfaces(superInterfaces);
//            }
//
//            @Override
//            public Builder addSuperInterfacesByClasses(Iterable<Class<?>> superInterfaces) {
//                return super.addSuperInterfacesByClasses(superInterfaces);
//            }
//
//            @Override
//            public Builder addSuperInterfacesByClasses(Class<?>... superInterfaces) {
//                return super.addSuperInterfacesByClasses(superInterfaces);
//            }
//
//            @Override
//            public Builder addField(Type type, String name, Modifier... modifiers) {
//                return super.addField(type, name, modifiers);
//            }
//
//            @Override
//            public Builder addField(TypeName type, String name, Modifier... modifiers) {
//                return super.addField(type, name, modifiers);
//            }
//
//            @Override
//            public ComplexJavaCodeBlockBuilder<Builder> beginInstanceInitializer() {
//                return super.beginInstanceInitializer();
//            }
//
//            @Override
//            public ComplexJavaCodeBlockBuilder<Builder> beginStaticInitializer() {
//                return super.beginStaticInitializer();
//            }
//
//            @Override
//            public Builder addConstructor(ExecutorSpec.ConstructorSpec constructorSpec) {
//                return super.addConstructor(constructorSpec);
//            }
//
//            @Override
//            public Builder addConstructors(ExecutorSpec.ConstructorSpec... constructorSpecs) {
//                return super.addConstructors(constructorSpecs);
//            }
//
//            @Override
//            public Builder addConstructors(Iterable<ExecutorSpec.ConstructorSpec> constructorSpecs) {
//                return super.addConstructors(constructorSpecs);
//            }
//
//            @Override
//            public Builder addMethod(ExecutorSpec.MethodSpec methodSpec) {
//                return super.addMethod(methodSpec);
//            }
//
//            @Override
//            public Builder addMethods(ExecutorSpec.MethodSpec... methodSpecs) {
//                return super.addMethods(methodSpecs);
//            }
//
//            @Override
//            public Builder addMethods(Iterable<ExecutorSpec.MethodSpec> methodSpecs) {
//                return super.addMethods(methodSpecs);
//            }
//
//            public ClassSpec build() {
//                return new ClassSpec(this);
//            }
//        }
//    }


//    public static class InterfaceSpec extends TypeSpec {
//
//        private InterfaceSpec(Builder builder) {
//            super(builder);
//        }
//
//        public static Builder builder(String name, Modifier... modifiers) {
//            return new Builder(name, modifiers);
//        }
//
//        public static class Builder extends AbstractBuilder<Builder> {
//            protected Builder(String name, Modifier... modifiers) {
//                super(INTERFACE, name, modifiers);
//            }
//
//            @Override
//            public Builder addTypeVariables(Iterable<TypeVariableName> typeVariableNames) {
//                return super.addTypeVariables(typeVariableNames);
//            }
//
//            @Override
//            public Builder addTypeVariable(TypeVariableName typeVariableName) {
//                return super.addTypeVariable(typeVariableName);
//            }
//
//            @Override
//            public Builder addTypeVariables(TypeVariableName... typeVariableNames) {
//                return super.addTypeVariables(typeVariableNames);
//            }
//
//            @Override
//            public Builder addSuperInterface(Class<?> superInterface) {
//                return super.addSuperInterface(superInterface);
//            }
//
//            @Override
//            public Builder addSuperInterface(TypeName superInterface) {
//                return super.addSuperInterface(superInterface);
//            }
//
//            @Override
//            public Builder addSuperInterfaces(Iterable<TypeName> superInterfaces) {
//                return super.addSuperInterfaces(superInterfaces);
//            }
//
//            @Override
//            public Builder addSuperInterfaces(TypeName... superInterfaces) {
//                return super.addSuperInterfaces(superInterfaces);
//            }
//
//            @Override
//            public Builder addSuperInterfacesByClasses(Iterable<Class<?>> superInterfaces) {
//                return super.addSuperInterfacesByClasses(superInterfaces);
//            }
//
//            @Override
//            public Builder addSuperInterfacesByClasses(Class<?>... superInterfaces) {
//                return super.addSuperInterfacesByClasses(superInterfaces);
//            }
//
//            @Override
//            public Builder addMethod(ExecutorSpec.MethodSpec methodSpec) {
//                return super.addMethod(methodSpec);
//            }
//
//            @Override
//            public Builder addMethods(ExecutorSpec.MethodSpec... methodSpecs) {
//                return super.addMethods(methodSpecs);
//            }
//
//            @Override
//            public Builder addMethods(Iterable<ExecutorSpec.MethodSpec> methodSpecs) {
//                return super.addMethods(methodSpecs);
//            }
//
//            public InterfaceSpec build() {
//                return new InterfaceSpec(this);
//            }
//        }
//    }

//    public static class EnumSpec extends TypeSpec {
//        private EnumSpec(Builder builder) {
//            super(builder);
//        }
//
//        public static Builder builder(String name, Modifier... modifiers) {
//            return new Builder(name, modifiers);
//        }
//
//        public static class Builder extends AbstractBuilder<Builder> {
//            protected Builder(String name, Modifier... modifiers) {
//                super(ENUM, name, modifiers);
//            }
//
//            public static Builder builder(String name) {
//                return new Builder(name);
//            }
//
//            @Override
//            public Builder addSuperInterface(Class<?> superInterface) {
//                return super.addSuperInterface(superInterface);
//            }
//
//            @Override
//            public Builder addSuperInterface(TypeName superInterface) {
//                return super.addSuperInterface(superInterface);
//            }
//
//            @Override
//            public Builder addSuperInterfaces(Iterable<TypeName> superInterfaces) {
//                return super.addSuperInterfaces(superInterfaces);
//            }
//
//            @Override
//            public Builder addSuperInterfaces(TypeName... superInterfaces) {
//                return super.addSuperInterfaces(superInterfaces);
//            }
//
//            @Override
//            public Builder addSuperInterfacesByClasses(Iterable<Class<?>> superInterfaces) {
//                return super.addSuperInterfacesByClasses(superInterfaces);
//            }
//
//            @Override
//            public Builder addSuperInterfacesByClasses(Class<?>... superInterfaces) {
//                return super.addSuperInterfacesByClasses(superInterfaces);
//            }
//
//            public Builder addEnumConstant(String name) {
//                return addEnumConstant(name, ClassSpec.anonymousBuilder().build());
//            }
//
//            public Builder addEnumConstant(String name, String format, Object... args) {
//                return addEnumConstant(name, JavaCodeBlock.of(format, args));
//            }
//
//            public Builder addEnumConstant(String name, JavaCodeBlock codeBlock) {
//                return addEnumConstant(name, ClassSpec.anonymousBuilder(codeBlock).build());
//            }
//
//            public Builder addEnumConstant(String name, TypeSpec typeSpec) {
//                if (this.enumConstants.put(argNotBlank(name, "name"), argNotNull(typeSpec, "typeSpec")) != null) {
//                    throw new IllegalArgumentException(String.format("The name: %s is used by other enum constant before.", name));
//                }
//                return this;
//            }
//
//            @Override
//            public Builder addField(Type type, String name, Modifier... modifiers) {
//                return super.addField(type, name, modifiers);
//            }
//
//            @Override
//            public Builder addField(TypeName type, String name, Modifier... modifiers) {
//                return super.addField(type, name, modifiers);
//            }
//
//            @Override
//            public ComplexJavaCodeBlockBuilder<Builder> beginStaticInitializer() {
//                return super.beginStaticInitializer();
//            }
//
//            @Override
//            public ComplexJavaCodeBlockBuilder<Builder> beginInstanceInitializer() {
//                return super.beginInstanceInitializer();
//            }
//
//            @Override
//            public Builder addConstructor(ExecutorSpec.ConstructorSpec constructorSpec) {
//                return super.addConstructor(constructorSpec);
//            }
//
//            @Override
//            public Builder addConstructors(ExecutorSpec.ConstructorSpec... constructorSpecs) {
//                return super.addConstructors(constructorSpecs);
//            }
//
//            @Override
//            public Builder addConstructors(Iterable<ExecutorSpec.ConstructorSpec> constructorSpecs) {
//                return super.addConstructors(constructorSpecs);
//            }
//
//            @Override
//            public Builder addMethod(ExecutorSpec.MethodSpec methodSpec) {
//                return super.addMethod(methodSpec);
//            }
//
//            @Override
//            public Builder addMethods(ExecutorSpec.MethodSpec... methodSpecs) {
//                return super.addMethods(methodSpecs);
//            }
//
//            @Override
//            public Builder addMethods(Iterable<ExecutorSpec.MethodSpec> methodSpecs) {
//                return super.addMethods(methodSpecs);
//            }
//
//            public EnumSpec build() {
//                return new EnumSpec(this);
//            }
//        }
//
//    }

//    public static class AnnotationSpec extends TypeSpec {
//        private AnnotationSpec(Builder builder) {
//            super(builder);
//        }
//
//        public static Builder builder(String name, Modifier... modifiers) {
//            return new Builder(name, modifiers);
//        }
//
//        public static class Builder extends AbstractBuilder<Builder> {
//            protected Builder(String name, Modifier... modifiers) {
//                super(ANNOTATION, name, modifiers);
//            }
//
//            public Builder addAttribute(ExecutorSpec.AttributeSpec attribute) {
//                this.attributes.add(argNotNull(attribute, "attribute"));
//                return this;
//            }
//
//            public Builder addAttributes(ExecutorSpec.AttributeSpec... attributes) {
//                return addAttributes(Arrays.asList(attributes));
//            }
//
//            public Builder addAttributes(Iterable<ExecutorSpec.AttributeSpec> attributes) {
//                int index = -1;
//                for (ExecutorSpec.AttributeSpec attribute : argNotNull(attributes, "attributes")) {
//                    addAttribute(argElementNotNull(attribute, ++index, "attributes"));
//                }
//                return this;
//            }
//
//            public AnnotationSpec build() {
//                return new AnnotationSpec(this);
//            }
//        }
//    }

}
