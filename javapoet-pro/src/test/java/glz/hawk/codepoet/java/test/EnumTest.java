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
import glz.hawkframework.core.support.ArgumentSupport;
import glz.hawk.codepoet.java.javadoc.FileJavadoc;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.util.UUID;

import static glz.hawk.codepoet.java.ProjectUtil.srcTestJava;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class EnumTest {

    /**
     * Simple enum, only includes enum constants and corresponding javadoc
     */
    @Test
    public void test1() {
        EnumSpec enumSpec = EnumSpec.builder("HelloEnumLevel")
            .addEnumConstant("A", ClassSpec.anonymousBuilder().setJavadoc("Hawk").build())
            .addEnumConstant("B", ClassSpec.anonymousBuilder().setJavadoc("eagle").build())
            .build();
        JavaFile javaFile = JavaFile.builder("com.example.test", enumSpec)
            .setJavadoc(FileJavadoc.builder()
                .beginJavadoc()
                .addDoc("Hello Enum Generator")
                .end()
                .build())
            .build();

        javaFile.writeTo(System.out);
        javaFile.writeTo(javaFile.delete(srcTestJava()));
    }

    /**
     * enum with a constructor
     */
    @Test
    public void test2() {
        EnumSpec enumSpec = EnumSpec.builder("HelloEnumComplexLevel")
            .addEnumConstant("A", "$S", "namedAAA")
            .addEnumConstant("B", "$S", "namedBBB")
            .addField(String.class, "name", Modifier.FINAL, Modifier.PRIVATE)
            .addConstructor(ConstructorSpec.builder()
                .addParameter(String.class, "name")
                .beginConstructorBody()
                .addStatement("this.name = $T.argNotBlank(name,\"name\")", ArgumentSupport.class)
                .end()
                .build())
            .addMethod(MethodSpec.builder(String.class, "getName", Modifier.PUBLIC)
                .beginMethodBody()
                .addStatement("return this.name")
                .end()
                .build())
            .build();
        JavaFile javaFile = JavaFile.builder("com.example.test", enumSpec)
            .build();

        javaFile.writeTo(System.out);
        javaFile.writeTo(javaFile.delete(srcTestJava()));
    }

    /**
     * enum with a constructor and an abstract method
     */
    @Test
    public void test3() {
        EnumSpec enumSpec = EnumSpec.builder("HelloEnumSpecialComplexLevel")
            .addModifier(Modifier.PUBLIC)
            .addEnumConstant("A", ClassSpec.anonymousBuilder("$S", "namedAAA")
                .setJavadoc("Hello World, namedAAA")
                .addMethod(MethodSpec.builder(String.class, "getRandomName", Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .beginMethodBody()
                    .addStatement("return this.name() + System.currentTimeMillis()")
                    .end()
                    .build())
                .build()
            )
            .addEnumConstant("B", ClassSpec.anonymousBuilder("$S", "namedBBB")
                .setJavadoc("namedBBB")
                .addMethod(MethodSpec.builder(String.class, "getRandomName", Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .beginMethodBody()
                    .addStatement("return this.name() + $T.randomUUID()", UUID.class)
                    .end()
                    .build())
                .build()
            )
            .addField(String.class, "name", Modifier.FINAL, Modifier.PRIVATE)
            .addConstructor(ConstructorSpec.builder()
                .addParameter(String.class, "name")
                .beginConstructorBody()
                .addStatement("this.name = $T.argNotBlank(name,\"name\")", ArgumentSupport.class)
                .end()
                .build())
            .addMethod(MethodSpec.builder(String.class, "getName", Modifier.PUBLIC)
                .beginMethodBody()
                .addStatement("return this.name")
                .end()
                .build())
            .addMethod(MethodSpec.builder(String.class, "getRandomName", Modifier.PUBLIC, Modifier.ABSTRACT).build())
            .build();
        JavaFile javaFile = JavaFile.builder("com.example.test", enumSpec)
            .build();

        javaFile.writeTo(System.out);
        javaFile.writeTo(javaFile.delete(srcTestJava()));
    }
}
