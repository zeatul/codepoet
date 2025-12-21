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
import glz.hawk.codepoet.java.type.ParameterizedTypeName;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.util.function.Consumer;

import static glz.hawk.codepoet.java.ProjectUtil.srcTestJava;
import static glz.hawk.codepoet.java.type.VoidTypeName.VOID;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class AnonymousInnerClassTest {

    @Test
    public void test() {
        ClassSpec classSpec = ClassSpec.builder("ExampleAnonymousInnerClass", Modifier.PUBLIC)
            .addField(FieldSpec.builder(ParameterizedTypeName.of(Consumer.class, Integer.class), "consumer")
                .setInitializer("$L", ClassSpec.anonymousBuilder()
                    .setSuperClass(ParameterizedTypeName.of(Consumer.class, Integer.class))
                    .addMethod(MethodSpec.builder(VOID, "accept",Modifier.PUBLIC)
                        .addParameter(Integer.class, "integer")
                        .beginMethodBody()
                        .end()
                        .build())
                    .build())
                .build())
            .addMethod(MethodSpec.builder(VOID,"m1")
                .addParameter(ParameterizedTypeName.of(Consumer.class, Integer.class),"consumer").build())
            .addMethod(MethodSpec.builder(VOID,"m2")
                .beginMethodBody()
                .addStatement("m1($L)", ClassSpec.anonymousBuilder()
                    .setSuperClass(ParameterizedTypeName.of(Consumer.class, Integer.class))
                    .addMethod(MethodSpec.builder(VOID, "accept",Modifier.PUBLIC)
                        .addParameter(Integer.class, "integer")
                        .beginMethodBody()
                        .end()
                        .build())
                    .build())
                .end().build())
            .build();

        JavaFile javaFile = JavaFile.builder("com.example.test", classSpec).build();
        javaFile.writeTo(System.out);
        javaFile.writeTo(javaFile.delete(srcTestJava()));
    }
}
