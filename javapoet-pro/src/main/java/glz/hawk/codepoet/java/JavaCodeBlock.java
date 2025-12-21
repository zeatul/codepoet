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

import glz.hawk.codepoet.core.AbstractCodeBlock;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.TypeName;
import glz.hawk.codepoet.java.type.TypeNameHelper;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A fragment of a .java file, potentially containing declarations, statements, and documentation.
 * Code blocks are not necessarily well-formed Java code, and are not validated. This class assumes
 * javac will check correctness later!
 *
 * <p>Code blocks support placeholders like {@link java.text.Format}. Where {@link String#format}
 * uses percent {@code %} to reference target values, this class uses dollar sign {@code $} and has
 * its own set of permitted placeholders:
 *
 * <ul>
 *   <li>{@code $L} emits a <em>literal</em> value with no escaping. Arguments for literals may be
 *       strings, primitives, {@linkplain TypeSpec type declarations}, {@linkplain AnnotationInstanceSpec
 *       annotations} and even other code blocks.
 *   <li>{@code $N} emits a <em>name</em>, using name collision avoidance where necessary. Arguments
 *       for names may be strings (actually any {@linkplain CharSequence character sequence}),
 *       {@linkplain ParameterSpec parameters}, {@linkplain FieldSpec fields}, {@linkplain
 *       MethodSpec methods}, and {@linkplain TypeSpec types}.
 *   <li>{@code $S} escapes the value as a <em>string</em>, wraps it with double quotes, and emits
 *       that. For example, {@code 6" sandwich} is emitted {@code "6\" sandwich"}.
 *   <li>{@code $T} emits a <em>type</em> reference. Types will be imported if possible. Arguments
 *       for types may be {@linkplain Class classes}, {@linkplain javax.lang.model.type.TypeMirror
 * ,*       type mirrors}, and {@linkplain javax.lang.model.element.Element elements}.
 *   <li>{@code $$} emits a dollar sign.
 *   <li>{@code $W} emits a space or a newline, depending on its position on the line. This prefers
 *       to wrap lines before 100 columns.
 *   <li>{@code $Z} acts as a zero-width space. This prefers to wrap lines before 100 columns.
 *   <li>{@code $>} increases the indentation level.
 *   <li>{@code $<} decreases the indentation level.
 *   <li>{@code $[} begins a statement. For multiline statements, every line after the first line
 *       is double-indented.
 *   <li>{@code $]} ends a statement.
 *   <li>{@code $R} emit a line separator.
 * </ul>
 *
 * @author Hawk
 */
public class JavaCodeBlock extends AbstractCodeBlock {

    private JavaCodeBlock(Builder builder) {
        super(builder);
    }

    public static JavaCodeBlock of(String format, Object... args) {
        return new Builder().add(format, args).build();
    }

    public static JavaCodeBlock ofNamed(String format, Map<String, ?> args) {
        return new Builder().addNamed(format, args).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Set<String> imports() {
        Set<String> imports = new HashSet<>();
        this.args.forEach(arg -> {
                if (arg instanceof Class<?>) {
                    imports.add(((Class<?>) arg).getCanonicalName());
                } else if (arg instanceof ClassName) {
                    imports.add(((ClassName) arg).canonicalName());
                } else if (arg instanceof AnnotationInstanceSpec) {
                    imports.addAll(((AnnotationInstanceSpec) arg).imports());
                }
                //TODO: any other potential import? 数组，泛型类用到的类型等
            }
        );
        return imports;
    }

    public Stream<String> importsAsStream() {
        return imports().stream();
    }


    public static final class Builder extends AbstractCodeBlockBuilder<JavaCodeBlock, Builder> {

        final static Character[] NO_ARG_PLACE_HOLDERS = new Character[]{'$', '>', '<', '[', ']', 'W', 'R'};
        final static Character[] ARG_PLACE_HOLDERS = new Character[]{'N', 'L', 'S', 'T'};

        private Builder() {
            super(NO_ARG_PLACE_HOLDERS, ARG_PLACE_HOLDERS);
        }

        public Builder newLine() {
            this.formatParts.add("$R");
            return this;
        }

        public Builder indent() {
            this.formatParts.add("$>");
            return this;
        }

        public Builder unindent() {
            this.formatParts.add("$<");
            return this;
        }

        @Override
        protected void addArgument(String format, char c, Object arg) {
            switch (c) {
                case 'N':
                    this.args.add(argToName(arg));
                    break;
                case 'L':
                    this.args.add(argToLiteral(arg));
                    break;
                case 'S':
                    this.args.add(argToString(arg));
                    break;
                case 'T':
                    this.args.add(argToType(arg));
                    break;
                default:
                    throw new IllegalArgumentException(
                        String.format("invalid format string: '%s'", format));
            }
        }

        private String argToName(Object o) {
            if (o instanceof CharSequence) return o.toString();
            if (o instanceof ParameterSpec) return ((ParameterSpec) o).name;
            if (o instanceof FieldSpec) return ((FieldSpec) o).name;
            if (o instanceof MethodSpec) return ((MethodSpec) o).name;
            if (o instanceof TypeSpec) return ((TypeSpec) o).name;
            throw new IllegalArgumentException("expected name but was " + o);
        }

        private Object argToLiteral(Object o) {
            return o;
        }

        private String argToString(Object o) {
            return o != null ? String.valueOf(o) : null;
        }

        private TypeName argToType(Object o) {
            if (o instanceof TypeName) {
                return (TypeName) o;
            }
            if (o instanceof Class) {
                return ClassName.ofClass((Class<?>) o);
            }
            if (o instanceof TypeMirror) {
                return TypeNameHelper.ofTypeMirror((TypeMirror) o);
            }
            if (o instanceof Element) {
                return TypeNameHelper.ofTypeMirror(((Element) o).asType());
            }
            if (o instanceof Type) {
                return TypeNameHelper.ofType((Type) o);
            }
            throw new IllegalArgumentException("expected type but was " + o);
        }

        public JavaCodeBlock build() {
            return new JavaCodeBlock(this);
        }
    }
}
