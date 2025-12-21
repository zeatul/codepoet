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
import glz.hawk.codepoet.java.JavaFile;
import glz.hawk.codepoet.java.MethodSpec;
import glz.hawk.codepoet.java.type.TypeVariableName;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;

import static glz.hawk.codepoet.java.ProjectUtil.srcTestJava;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class MethodTest {

    /**
     * test type variable
     */
    @Test
    public void test1() throws IOException {
        TypeVariableName param1 = TypeVariableName.of("T", String.class);
        TypeVariableName param2 = TypeVariableName.of("N", Number.class);
        MethodSpec methodSpec = MethodSpec.builder(String.class, "compute", Modifier.PUBLIC)
            .addTypeVariable(param1)
            .addTypeVariable(param2)
            .addParameter(param1, "param1")
            .addParameter(param2, "param2")
            .beginMethodBody()
            .addStatement("return $S", "hello world!")
            .end().build();

        ClassSpec classSpec = ClassSpec.builder("HelloMethodDemo")
            .addMethod(methodSpec)
            .build();

        JavaFile javaFile = JavaFile.builder("com.example.test", classSpec)
            .build();

        javaFile.writeTo(System.out);
        javaFile.writeTo(javaFile.delete(srcTestJava()));
    }

}
