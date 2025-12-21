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

import glz.hawk.codepoet.java.javadoc.FieldJavadoc;
import glz.hawk.codepoet.java.type.TypeName;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static glz.hawk.codepoet.java.type.TypeNameHelper.ofType;
import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class FieldSpec extends MemberSpec<FieldJavadoc> {

    public final TypeName type;
    public final String name;
    public final JavaCodeBlock initializer;

    private FieldSpec(Builder builder) {
        super(builder);
        this.type = builder.type;
        this.name = builder.name;
        this.initializer = builder.initializerBuilder.build();
    }

    public static Builder builder(TypeName type, String name, Modifier... modifiers) {
        return new Builder(type, name).addModifiers(modifiers);
    }

    public static Builder builder(Type type, String name, Modifier... modifiers) {
        return builder(ofType(type), name, modifiers);
    }

    Set<String> imports() {
        HashSet<String> imports = new HashSet<>(super.imports());
        // type
        imports.addAll(type.imports());
        // initializer
        imports.addAll(initializer.imports());
        return imports;
    }

    void emit(JavaCodeWriter codeWriter, Set<Modifier> legalModifiers, Set<Modifier> implicitModifiers) throws IOException {
        codeWriter.emitJavadoc(javadoc);
        codeWriter.emitAnnotations(annotations, false);
        codeWriter.emitModifiers(modifiers, legalModifiers, implicitModifiers);
        codeWriter.emit("$T $L", type, name);
        if (!initializer.isEmpty()) {
            codeWriter.emit(" = ");
            codeWriter.emit(initializer);
        }
        codeWriter.emit(";").emitNewLine();
    }

    public static final class Builder extends MemberBuilder<Builder, FieldJavadoc> {
        private final TypeName type;
        private final String name;
        private final JavaCodeBlock.Builder initializerBuilder = JavaCodeBlock.builder();

        private Builder(TypeName type, String name) {
            super(FieldJavadoc.builder().build());
            this.type = argNotNull(type, "type");
            //TODO:校验name
            this.name = argNotBlank(name, "name");
        }

        public Builder setInitializer(String format, Object... args) {
            initializerBuilder.add(format, args);
            return this;
        }

        public Builder setInitializer(JavaCodeBlock codeBlock) {
            initializerBuilder.add(codeBlock);
            return this;
        }

        @Override
        protected void verifyModifier(Modifier modifier) {
            argument(modifier, MemberCategory.FIELD::isValid, m -> String.format("The modifier: %s is illegal for field", modifier));
        }

        public Builder setJavadoc(Function<FieldJavadoc.Builder, FieldJavadoc> function) {
            setJavadoc(function.apply(FieldJavadoc.builder()));
            return this;
        }

        @Override
        protected FieldJavadoc.Builder getJavadocBuilder() {
            return FieldJavadoc.builder();
        }

        public FieldSpec build() {
            //TODO:校验
            return new FieldSpec(this);
        }
    }

}
