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

import glz.hawk.codepoet.java.*;
import glz.hawk.codepoet.java.javadoc.FileJavadoc;
import glz.hawk.codepoet.java.support.annotations.JsonFormat;
import glz.hawk.codepoet.java.support.annotations.NotNull;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.ParameterizedTypeName;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static glz.hawk.codepoet.java.ProjectUtil.srcTestJava;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class AnnotatedTypeNameTest {
    @Test
    public void test() throws IOException {

        TypeSpec typeSpec = ClassSpec.builder("ExampleAnnotatedTypeName", Modifier.PUBLIC)
            .addField(ParameterizedTypeName.of(List.class, String.class), "f1")
            .addField(ParameterizedTypeName.of(List.class,
                    AnnotatedTypeName.of(
                        ClassName.ofClass(LocalDate.class),
                        AnnotationInstanceSpec.builder(JsonFormat.class).addMember("pattern", "yyyy-MM-dd").build(),
                        AnnotationInstanceSpec.builder(NotNull.class).build()
                    )),
                "f2")
            .addMethod(
                MethodSpec.builder(
                        AnnotatedTypeName.of(
                            ClassName.ofClass(LocalDate.class),
                            AnnotationInstanceSpec.builder(JsonFormat.class).addMember("pattern", "yyyy-MM-dd").build(),
                            AnnotationInstanceSpec.builder(NotNull.class).build()
                        ),
                        "today",
                        Modifier.PUBLIC
                    ).beginMethodBody()
                    .addStatement("return $T.now()", LocalDate.class)
                    .end()
                    .build()
            )
            .build();
        JavaFile javaFile = JavaFile.builder("com.example.test", typeSpec)
            .setJavadoc(FileJavadoc.builder().beginJavadoc().addDoc("File Comment").end().build())
            .build();

        javaFile.writeTo(System.out);
        javaFile.writeTo(javaFile.delete(srcTestJava()));
    }
}
