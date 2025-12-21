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
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.TypeName;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class AttributeSpec extends ExecutorSpec<AttributeJavadoc> {
    protected AttributeSpec(Builder builder) {
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
        argFalse(() -> name.equals(className), () -> "The attribute name can't be same as class name.");
        codeWriter.emit("$T $L(", returnType, name);
    }

    public static final class Builder extends ExecutorBuilder<Builder, AttributeJavadoc> {

        private Builder(TypeName returnType, String name, Modifier... modifiers) {
            super(AttributeJavadoc.builder().build(), returnType, name, modifiers);
            //TODO:校验name
            //TODO:如何保证名称不和类名相同
            argNotBlank(name, "name");
            //TODO:returnType的默认值 ,不可以为 void，java.lang.Void
            argNotNull(returnType, "returnType");
        }

        public AttributeSpec build() {
            return new AttributeSpec(this);
        }

        public Builder setJavadoc(Function<AttributeJavadoc.Builder, AttributeJavadoc> function) {
            setJavadoc(function.apply(AttributeJavadoc.builder()));
            return this;
        }

        @Override
        protected void verifyModifier(Modifier modifier) {
            argument(modifier, MemberCategory.ATTRIBUTE::isValid, m -> String.format("The modifier: %s is illegal for annotation attribute", modifier));
        }

        @Override
        protected AttributeJavadoc.Builder getJavadocBuilder() {
            return AttributeJavadoc.builder();
        }

        public Builder defaultValue(String format, Object... args) {
            return defaultValue(JavaCodeBlock.of(format, args));
        }

        public Builder defaultValue(JavaCodeBlock codeBlock) {
            this.defaultValueBuilder.add(codeBlock);
            return this;
        }
    }
}
