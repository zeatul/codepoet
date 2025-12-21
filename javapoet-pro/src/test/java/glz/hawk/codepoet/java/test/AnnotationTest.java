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

import glz.hawk.codepoet.java.AnnotationSpec;
import glz.hawk.codepoet.java.AttributeSpec;
import glz.hawk.codepoet.java.JavaFile;
import glz.hawk.codepoet.java.javadoc.AttributeJavadoc;
import glz.hawk.codepoet.java.javadoc.FileJavadoc;
import glz.hawk.codepoet.java.type.ArrayTypeName;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;

import static glz.hawk.codepoet.java.ProjectUtil.srcTestJava;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class AnnotationTest {

    @Test
    public void test() throws IOException {
        AnnotationSpec typeSpec = AnnotationSpec.builder("HelloAnnotation").addModifier(Modifier.PUBLIC)
            .addAttribute(AttributeSpec.builder(String.class, "name")
                .defaultValue("$S", "Venus")
                .build())
            .addAttribute(AttributeSpec.builder(String.class, "type")
                .defaultValue("$S", "Star")
                .setJavadoc(AttributeJavadoc.builder().beginJavadoc().addDoc("类型定义\"").end().build())
                .build())
            .addAttribute(AttributeSpec.builder(ArrayTypeName.ofType(String.class), "nameList")
                .defaultValue("$L", "{}")
                .build())
            .build();
        JavaFile javaFile = JavaFile.builder("com.example.test", typeSpec)
            .setJavadoc(FileJavadoc.builder().beginJavadoc().addDoc("File Comment").end().build())
            .build();

        javaFile.writeTo(System.out);
        javaFile.writeTo(javaFile.delete(srcTestJava()));
    }
}
