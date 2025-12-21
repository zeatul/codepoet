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

import glz.hawkframework.core.helper.ObjectHelper;
import glz.hawk.codepoet.core.AbstractCodeWriter;
import glz.hawk.codepoet.java.javadoc.BlockTag;
import glz.hawk.codepoet.java.javadoc.BlockTagType;
import glz.hawk.codepoet.java.javadoc.Javadoc;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.TypeName;
import glz.hawk.codepoet.java.type.TypeVariableName;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.lang.Character.isISOControl;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class JavaCodeWriter extends AbstractCodeWriter<JavaCodeWriter> {


    private boolean isWritingJavadoc = false;

    JavaCodeWriter(Appendable out, String indent, String lineSeparator) {
        super(out, indent, lineSeparator);
    }

    public JavaCodeWriter emit(boolean booleanExpression, String format, Object... args) throws IOException {
        if (booleanExpression) {
            return emit(JavaCodeBlock.of(format, args));
        }
        return this;
    }

    public JavaCodeWriter emit(String format, Object... args) throws IOException {
        return emit(JavaCodeBlock.of(format, args));
    }

    @Override
    public JavaCodeWriter emitNewLine() throws IOException {
        if (trailingNewline && isWritingJavadoc) {
            out.append(" * ");
        }
        return super.emitNewLine();
    }

    public JavaCodeWriter emit(JavaCodeBlock codeBlock) throws IOException {
        int a = 0;
        for (String part : codeBlock.formatParts) {
            switch (part) {
                case "$L":
                    emitLiteral(codeBlock.args.get(a++));
                    break;
                case "$N":
                    emitAndIndent((String) codeBlock.args.get(a++));
                    break;
                case "$S":
                    emitAndIndent(stringLiteralWithDoubleQuotes((String) codeBlock.args.get(a++)));
                    break;
                case "$T":
                    TypeName typeName = (TypeName) codeBlock.args.get(a++);
                    emitAndIndent(typeName.keyword());
                    break;
                case "$$":
                    emitAndIndent("$");
                    break;
                case "$>":
                    indent();
                    break;
                case "$<":
                    unindent();
                    break;
                case "$R":
                    emitNewLine();
                    break;
                default:
                    emitAndIndent(part);
                    break;
            }
        }
        return this;
    }

    void emitLiteral(Object o) throws IOException {

        if (o instanceof AnnotationInstanceSpec) {
            ((AnnotationInstanceSpec) o).emit(this);
        } else if (o instanceof JavaCodeBlock) {
            emit((JavaCodeBlock) o);
        } else if (o instanceof TypeSpec) {
            //输出内部类
            ((TypeSpec) o).emit(this, null, true);
        } else {
            emitAndIndent(String.valueOf(o));
        }
    }

    String stringLiteralWithDoubleQuotes(String value) {
        if (value == null) {
            return "null";
        }
        StringBuilder result = new StringBuilder(value.length() + 2);
        result.append('\"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\b':
                    result.append("\\b");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\"':
                    result.append("\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                default:
                    result.append(isISOControl(c) ? String.format("\\u%04x", (int) c) : Character.toString(c));
            }
        }
        result.append('\"');
        return result.toString();
    }

    @Override
    public JavaCodeWriter emitAndIndent(String s) throws IOException {
        if (trailingNewline) {
            emitIndentation();
            if (isWritingJavadoc) {
                out.append(" * ");
            }
        }
        out.append(s);
        trailingNewline = false;
        return this;
    }

    void emitJavadoc(Javadoc javadoc) throws IOException {
        if (javadoc.isEmpty()) {
            return;
        }
        emit("/**").emitNewLine();
        isWritingJavadoc = true;
        emit(javadoc.doc);

        //TODO: emit block tags
        if (!javadoc.blockTags.isEmpty()) {
            emit(" ");
            emitNewLine();
        }

        for (BlockTag blockTag : javadoc.blockTags) {
            emit("$L ", blockTag.type.type);
            if (blockTag.name != null && !blockTag.name.isEmpty()) {
                if (blockTag.type == BlockTagType.THROWS) {
                    emit("$T ", blockTag.throwableClassName);
                } else {
                    emit("$L ", blockTag.name);
                }
            }
            emit(blockTag.content);
            emitNewLine();
        }

        if (!trailingNewline) {
            emitNewLine();
        }

        isWritingJavadoc = false;
        emit(" */").emitNewLine();
    }

    void emitAnnotations(List<AnnotationInstanceSpec> annotations, boolean inline) throws IOException {
        if (ObjectHelper.isEmpty(annotations)) {
            return;
        }
        for (AnnotationInstanceSpec annotationSpec : annotations) {
            annotationSpec.emit(this);
            if (inline) {
                emit(" ");
            } else {
                emitNewLine();
            }
        }
    }


    void emitModifiers(Set<Modifier> modifiers, Set<Modifier> legalModifiers, Set<Modifier> implicitModifiers) throws IOException {
        if (ObjectHelper.isEmpty(modifiers)) {
            return;
        }

        for (Modifier modifier : modifiers) {
            if (implicitModifiers.contains(modifier)) {
                continue;
            }
            if (legalModifiers.contains(modifier)) {
                emit("$L ", modifier.name().toLowerCase());
            } else {
                throw new IllegalStateException(String.format("Modifier: %s is illegal for current java element.", modifier));
            }
        }
    }

    void emitTypeCategory(TypeCategory typeCategory) throws IOException {
        emit("$L ", typeCategory.name);
    }

    void emitTypeVariables(List<TypeVariableName> typeVariables) throws IOException {
        if (ObjectHelper.isEmpty(typeVariables)) {
            return;
        }
        emit("<");
        for (int i = 0; i < typeVariables.size(); i++) {
            TypeVariableName typeVariableName = typeVariables.get(i);
            emit(typeVariableName.keyword());
            if (ObjectHelper.isNotEmpty(typeVariableName.bounds)) {
                emit(" extends ");
                for (int j = 0; j < typeVariableName.bounds.size(); j++) {
                    emit(typeVariableName.bounds.get(j).keyword());
                    if (j < typeVariableName.bounds.size() - 1) {
                        emit(" & ");
                    }
                }
            }
            if (i < typeVariables.size() - 1) {
                emit(", ");
            }
        }
        emit(">");
    }

    void emitSuperInterfaces(List<TypeName> superInterfaces, boolean isInterface) throws IOException {
        boolean first = true;
        for (TypeName superInterface : superInterfaces) {
            if (first) {
                String format = isInterface ? " extends $T" : " implements $T";
                emit(format, superInterface);
                first = false;
            } else {
                emit(", $T", superInterface);
            }
        }
    }

    void emitSuperClass(TypeName superClass) throws IOException {
        if (superClass == null) {
            return;
        } else if (superClass instanceof ClassName && ((ClassName) superClass).canonicalName().equals(Object.class.getCanonicalName())) {
            return;
        } else {
            emit(" extends $T", superClass);
        }
    }
}
