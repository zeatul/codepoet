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

package glz.hawk.codepoet.java.test;

import glz.hawk.codepoet.java.ClassSpec;
import glz.hawk.codepoet.java.FieldSpec;
import glz.hawk.codepoet.java.JavaFile;
import glz.hawk.codepoet.java.MethodSpec;
import glz.hawk.codepoet.java.javadoc.*;
import glz.hawk.codepoet.java.type.PrimitiveTypeName;
import glz.hawk.codepoet.java.type.TypeVariableName;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static glz.hawk.codepoet.java.ProjectUtil.srcTestJava;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class JavadocTest {

    @Test
    public void test() {
        ClassSpec classSpec = ClassSpec.builder("JavadocExample", Modifier.PUBLIC)
            .addTypeVariable(TypeVariableName.of("K"))
            .addTypeVariable(TypeVariableName.of("V"))
            .setJavadoc(TypeJavadoc.builder()
                .addBlockTag(BlockTag.builder(BlockTagType.PARAM, "<K>").add("type parameter 1").build())
                .addBlockTag(BlockTag.builder(BlockTagType.PARAM, "<V>").add("type parameter 2").build())
                .addBlockTag(BlockTag.builder(BlockTagType.AUTHOR).add("Hawk").build())
                .beginJavadoc()
                .addDoc("First").addDoc(", Second").addDoc(", Three").newLine()
                .addDocument("Type comment.")
                .addDocument("The addDocument method adds a new line in the end, but addDoc method doesn't add it.")
                .openBlockHtml(HtmlTag.P)
                .addInlineTag(InlineTag.LINK, "$T map", Map.class).addDoc("'s value is ").addInlineTag(InlineTag.CODE, "true").newLine()
                .addDocument("{@link $T list}'s value is {@code true}", List.class)
                .closeTag()
                .end()
                .build())
            .addField(FieldSpec.builder(String.class, "name", Modifier.PUBLIC)
                .setJavadoc(FieldJavadoc.builder()
                    .addBlockTag(BlockTag.builder(BlockTagType.SINCE).add("JDK1.1").build())
                    .beginJavadoc()
                    .addDocument("My name is Wanted")
                    .end()
                    .build())
                .build())
            .addMethod(MethodSpec.builder(PrimitiveTypeName.LONG, "sum")
                .addParameter(PrimitiveTypeName.LONG, "a")
                .addParameter(PrimitiveTypeName.LONG, "b")
                .addThrowable(Exception.class)
                .setJavadoc(MethodJavadoc.builder()
                    .addBlockTag(BlockTag.builder(BlockTagType.PARAM, "a").add("parameter a").build())
                    .addBlockTag(BlockTag.builder(BlockTagType.PARAM, "b").add("parameter b").build())
                    .addBlockTag(BlockTag.builder(IOException.class).add("io error was found.").build())
                    .addBlockTag(BlockTag.builder(BlockTagType.RETURN).add("the sum value").build())
                    .addBlockTag(BlockTag.builder(BlockTagType.DEPRECATED).build())
                    .beginJavadoc()
                    .addDocument("The method javadoc example.")
                    .end()
                    .build())
                .beginMethodBody()
                .addStatement("return a + b")
                .end()
                .build())
            .build();

        JavaFile javaFile = JavaFile.builder("com.example.test", classSpec)
            .setJavadoc(FileJavadoc.builder()
                .beginJavadoc()
                .addDocument("File Comment One.")
                .addDocument("File Comment Two.")
                .end()
                .build())
            .build();

        javaFile.writeTo(System.out);
        javaFile.writeTo(javaFile.delete(srcTestJava()));
    }

}
