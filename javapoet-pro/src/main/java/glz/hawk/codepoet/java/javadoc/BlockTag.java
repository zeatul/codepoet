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

package glz.hawk.codepoet.java.javadoc;

import glz.hawkframework.core.helper.StringHelper;
import glz.hawk.codepoet.java.JavaCodeBlock;
import glz.hawk.codepoet.java.type.ClassName;

import java.util.HashSet;
import java.util.Set;

import static glz.hawkframework.core.support.ArgumentSupport.*;
import static glz.hawkframework.core.support.LogicSupport.consumeIfNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class BlockTag {
    public final BlockTagType type;
    public final String name;
    public final JavaCodeBlock content;
    public final ClassName throwableClassName;

    private BlockTag(Builder builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.content = builder.contentBuilder.build();
        this.throwableClassName = builder.throwableClassName;
        if (type != BlockTagType.DEPRECATED) {
            argTrue(content.isNotEmpty(), () -> String.format("java doc parameter: %s has no content.", StringHelper.isBlank(name) ? type : type + "/" + name));
        }
    }

    public static Builder builder(BlockTagType type) {
        return new Builder(type);
    }

    public static Builder builder(Class<? extends Throwable> trowableClass) {
        return new Builder(ClassName.ofClass(argNotNull(trowableClass, "throwableClass")));
    }

    public static Builder builder(BlockTagType type, String name) {
        return new Builder(type, name);
    }

    Set<String> imports() {
        HashSet<String> imports = new HashSet<>();
        consumeIfNotNull(throwableClassName, t -> imports.add(t.canonicalName()));
        imports.addAll(content.imports());
        return imports;
    }

    public static class Builder {
        private final BlockTagType type;
        private final String name;
        private final JavaCodeBlock.Builder contentBuilder = JavaCodeBlock.builder();
        private final ClassName throwableClassName;

        private Builder(BlockTagType type) {
            this.type = argument(argNotNull(type, "type"), t -> !t.hasName, t -> String.format("The java doc parameter with type: %s must hava name.", t));
            this.name = null;
            this.throwableClassName = null;
        }

        private Builder(ClassName throwableClassName) {
            this.throwableClassName = argNotNull(throwableClassName, "throwableClassName");
            this.type = BlockTagType.THROWS;
            this.name = throwableClassName.canonicalName();

        }

        private Builder(BlockTagType type, String name) {
            this.type = argument(argNotNull(type, "type"), t -> t.hasName, t -> String.format("The java doc parameter with type: %s can't hava name.", t));
            if (type == BlockTagType.THROWS) {
                throw new IllegalArgumentException("Please use Builder(ClassName className) constructor");
            }
            this.name = argNotBlank(name, "name");
            this.throwableClassName = null;
        }

        public Builder add(String format, Object... args) {
            return add(JavaCodeBlock.of(format, args));
        }

        public Builder add(JavaCodeBlock codeBlock) {
            contentBuilder.add(argNotNull(codeBlock, "codeBlock"));
            return this;
        }

        public BlockTag build() {
            return new BlockTag(this);
        }

    }

}
