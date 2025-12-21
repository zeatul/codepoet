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

import glz.hawk.codepoet.java.JavaCodeBlock;

import java.util.*;

import static glz.hawkframework.core.support.ArgumentSupport.argElementNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class Javadoc {
    public final static String NO_NAME_TAG = "NO_NAME_TAG";
    public final JavaCodeBlock doc;
    public final List<BlockTag> blockTags;
    public final Location location;

    protected Javadoc(AbstractJavadocBuilder<?> builder) {
        this.doc = builder.docBuilder.buildCodeBlock();
        //TODO: map 转 blockTags : 排序
        this.blockTags = new ArrayList<>();
        builder.map.keySet().stream().sorted((a, b) -> a.getOrder().compareTo(b.getOrder())).map(builder.map::get).flatMap(v -> v.values().stream()).forEach(this.blockTags::add);
        this.location = builder.location;
    }

    public Set<String> imports() {
        HashSet<String> imports = new HashSet<>(doc.imports());
        blockTags.stream().map(BlockTag::imports).forEach(imports::addAll);
        return imports;
    }

    public boolean isEmpty() {
        return doc.isEmpty() && blockTags.isEmpty();
    }

    public enum Location {
        FILE,
        TYPE,
        FIELD,
        METHOD,
        CONSTRUCTOR,
        ENUM_CONSTANT,
        ATTRIBUTE
    }

    @SuppressWarnings("unchecked")
    public static abstract class AbstractJavadocBuilder<T extends AbstractJavadocBuilder<T>> {
        private final ComplexJavadocBlockBuilder<T> docBuilder = new ComplexJavadocBlockBuilder<>(JavaCodeBlock.builder(), (T) this);
        private final Map<BlockTagType, Map<String, BlockTag>> map = new HashMap<>();
        private final Location location;

        protected AbstractJavadocBuilder(Location location) {
            this.location = argNotNull(location, "location");
        }

        public ComplexJavadocBlockBuilder<T> beginJavadoc() {
            return this.docBuilder;
        }

        public T addBlockTag(BlockTag blockTag) {
            argNotNull(blockTag, "blockTag");
//            argTrue(blockTag.type.support(location), () -> String.format("The javadoc location: %s doesn't support tag: %s.", location, blockTag.type));
            Map<String, BlockTag> a = map.computeIfAbsent(blockTag.type, t -> new LinkedHashMap<>());
            String key = blockTag.name == null ? NO_NAME_TAG : blockTag.name;
            if (a.putIfAbsent(key, blockTag) != null) {
                throw new IllegalStateException(String.format("The blockTag with name: %s exists", key));
            }
            return (T) this;
        }

        public T addBlockTags(Iterable<BlockTag> blockTags) {
            int index = -1;
            for (BlockTag blockTag : argNotNull(blockTags, "blockTags")) {
                addBlockTag(argElementNotNull(blockTag, index++, "blockTags"));
            }
            return (T) this;
        }

        public T addBlockTags(BlockTag... blockTags) {
            return addBlockTags(Arrays.asList(blockTags));
        }

        public abstract Javadoc build();
    }

//    public static class FileJavadoc extends Javadoc {
//
//        protected FileJavadoc(Builder builder) {
//            super(builder);
//        }
//
//        public static Builder builder() {
//            return new Builder();
//        }
//
//        public static class Builder extends AbstractJavadocBuilder<Builder> {
//
//            private Builder() {
//                super(Location.FILE);
//            }
//
//            public FileJavadoc build() {
//                return new FileJavadoc(this);
//            }
//        }
//    }

//    public static class TypeJavadoc extends Javadoc {
//
//        protected TypeJavadoc(Builder builder) {
//            super(builder);
//        }
//
//        public static Builder builder() {
//            return new Builder();
//        }
//
//        public boolean isEmpty() {
//            return this.blockTags.isEmpty() && this.doc.isEmpty();
//        }
//
//        public static class Builder extends AbstractJavadocBuilder<Builder> {
//
//            private Builder() {
//                super(Location.TYPE);
//            }
//
//            public TypeJavadoc build() {
//                return new TypeJavadoc(this);
//            }
//        }
//    }

//    public static class ConstructorJavadoc extends Javadoc {
//
//        private ConstructorJavadoc(Builder builder) {
//            super(builder);
//        }
//
//        public static Builder builder() {
//            return new Builder();
//        }
//
//        public static class Builder extends AbstractJavadocBuilder<Builder> {
//
//            private Builder() {
//                super(Location.CONSTRUCTOR);
//            }
//
//            public ConstructorJavadoc build() {
//                return new ConstructorJavadoc(this);
//            }
//        }
//    }


//    public static class MethodJavadoc extends Javadoc {
//
//        protected MethodJavadoc(AbstractJavadocBuilder<?> builder) {
//            super(builder);
//        }
//
//        public static Builder builder() {
//            return new Builder();
//        }
//
//        public static class Builder extends AbstractJavadocBuilder<Builder> {
//
//            private Builder() {
//                super(Location.METHOD);
//            }
//
//            public MethodJavadoc build() {
//                return new MethodJavadoc(this);
//            }
//        }
//    }

//    public static class FieldJavadoc extends Javadoc {
//
//        protected FieldJavadoc(Builder builder) {
//            super(builder);
//        }
//
//        public static Builder builder() {
//            return new Builder();
//        }
//
//        public static class Builder extends AbstractJavadocBuilder<Builder> {
//
//            private Builder() {
//                super(Location.FIELD);
//            }
//
//            public FieldJavadoc build() {
//                return new FieldJavadoc(this);
//            }
//        }
//    }

//    public static class AttributeJavadoc extends Javadoc {
//
//        protected AttributeJavadoc(AbstractJavadocBuilder<?> builder) {
//            super(builder);
//        }
//
//        public static Builder builder() {
//            return new Builder();
//        }
//
//        public static class Builder extends AbstractJavadocBuilder<Builder> {
//            private Builder() {
//                super(Location.ATTRIBUTE);
//            }
//
//            public AttributeJavadoc build() {
//                return new AttributeJavadoc(this);
//            }
//        }
//    }
}
