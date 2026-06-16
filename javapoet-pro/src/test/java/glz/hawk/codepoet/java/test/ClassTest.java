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
import glz.hawk.codepoet.java.javadoc.ConstructorJavadoc;
import glz.hawk.codepoet.java.javadoc.FieldJavadoc;
import glz.hawk.codepoet.java.javadoc.FileJavadoc;
import glz.hawk.codepoet.java.javadoc.TypeJavadoc;
import glz.hawk.codepoet.java.support.Book;
import glz.hawk.codepoet.java.support.HelloClass;
import glz.hawk.codepoet.java.support.State;
import glz.hawk.codepoet.java.support.aaa.Worker;
import glz.hawk.codepoet.java.support.annotations.Display;
import glz.hawk.codepoet.java.support.annotations.DisplayAndLength;
import glz.hawk.codepoet.java.support.annotations.Length;
import glz.hawk.codepoet.java.support.enums.Level;
import glz.hawk.codepoet.java.support.interfaces.Ordered;
import glz.hawk.codepoet.java.support.interfaces.Summary;
import glz.hawk.codepoet.java.type.*;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static glz.hawk.codepoet.java.ProjectUtil.srcTestJava;
import static glz.hawk.codepoet.java.type.PrimitiveTypeName.*;
import static glz.hawk.codepoet.java.type.VoidTypeName.VOID;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ClassTest {

    @Test
    void test() {

        AnnotationInstanceSpec annotationSpec1 = AnnotationInstanceSpec.builder(Length.class)
            .addMember("length", 10)
            .addMember("name", "Cat")
            .addMember("name", "Dog")
            .build();

        AnnotationInstanceSpec annotationSpec11 = AnnotationInstanceSpec.builder(Length.class)
            .addMember("length", 110)
            .addMember("name", "Tiger")
            .build();

        AnnotationInstanceSpec annotationSpec2 = AnnotationInstanceSpec.builder(Display.class)
            .addMember("name", "Cat")
            .addMember("levels", Level.A)
            .addMember("levels", Level.OTHER)
            .build();

        AnnotationInstanceSpec annotationSpec3 = AnnotationInstanceSpec.builder(DisplayAndLength.class)
            .addMember("code", "NO1025")
            .addMember("lengths", annotationSpec1)
            .addMember("lengths", annotationSpec11)
            .addMember("displays", annotationSpec2)
            .build();

        FieldSpec field1 = FieldSpec.builder(Book.class, "book1", Modifier.PRIVATE)
            .setJavadoc(FieldJavadoc.builder()
                .beginJavadoc()
                .addDoc("历史书")
                .end()
                .build())
            .addAnnotation(annotationSpec1).build();

        FieldSpec field2 = FieldSpec.builder(Book.class, "book2", Modifier.PRIVATE, Modifier.FINAL)
            .setJavadoc(FieldJavadoc.builder()
                .beginJavadoc()
                .addDoc("计算机书")
                .end()
                .build())
            .addAnnotation(annotationSpec2)
            .setInitializer("$L", "new Book()")
            .build();

        FieldSpec field3 = FieldSpec.builder(INT, "count", Modifier.PRIVATE).build();

        ConstructorSpec constructor1 = ConstructorSpec.builder(Modifier.PUBLIC)
            .setJavadoc(ConstructorJavadoc.builder()
                .beginJavadoc()
                .addDoc("创建历史书")
                .end()
                .build())
            .build();

        ConstructorSpec constructor2 = ConstructorSpec.builder(Modifier.PUBLIC)
            .setJavadoc(ConstructorJavadoc.builder()
                .beginJavadoc()
                .addDoc("创建历史书")
                .end()
                .build())
            .addParameter(Book.class, "book1")
            .addParameter(INT, "count")
            .beginConstructorBody()
            .addStatement("this.book1 = book1")
            .addStatement("this.count = count")
            .end()
            .build();

        MethodSpec method1 = MethodSpec.builder(INT, "sum", Modifier.PRIVATE)
            .addParameter(INT, "a1")
            .addParameter(INT, "a2")
            .beginMethodBody()
            .addStatement("return a1 + a2")
            .end()
            .build();

        MethodSpec method2 = MethodSpec.builder(Level.class, "printLevel1", Modifier.PROTECTED)
            .addParameter(Level.class, "level")
            .beginMethodBody()
            .beginIf("level == $T.A", Level.class)
            .addStatement("System.out.println(\"A\" + level.name())")
            .beginElseIf("level == $T.B", Level.class)
            .addStatement("System.out.println(\"B\" + level.name())")
            .beginElse()
            .addStatement("System.out.println(level.name())")
            .endIf()
            .addStatement("return level")
            .end()
            .build();

        MethodSpec method3 = MethodSpec.builder(VOID, "printLevel2")
            .addParameter(Level.class, "level")
            .beginMethodBody()
            .beginSwitch("level")
            .beginCase("A")
            .addStatement("System.out.println(\"Ar\" + level.name())")
            .breakCase()
            .beginCase("B")
            .addStatement("System.out.println(\"B\" + level.name())")
            .breakCase()
            .beginDefault()
            .addStatement("System.out.println(\"Other\" + level.name())")
            .endSwitch()
            .end()
            .build();


        MethodSpec method4 = MethodSpec.builder(ParameterizedTypeName.of(List.class, String.class), "readText")
            .addParameter(File.class, "file")
            .addParameter(String.class, "charset")
            .beginMethodBody()
            .addStatement("$T fis = null", FileInputStream.class)
            .beginTry()
            .addStatement("$T<String> lines = new $T<>()", List.class, ArrayList.class)
            .addStatement("fis = new $T(file)", FileInputStream.class)
            .addStatement("$T br = new $T(new $T(fis, charset))", BufferedReader.class, BufferedReader.class, InputStreamReader.class)
            .addStatement("String line = br.readLine()")
            .beginWhile("line != null")
            .addStatement("lines.add(line)")
            .addStatement("line = br.readLine()")
            .endWhile()
            .addStatement("return lines")
            .beginCatch("$T e", FileNotFoundException.class)
            .addStatement("throw new $T(e)", UncheckedIOException.class)
            .beginCatch("$T e", UnsupportedEncodingException.class)
            .addStatement("throw new $T(e)", UncheckedIOException.class)
            .beginCatch("$T e", IOException.class)
            .addStatement("throw new $T(e)", UncheckedIOException.class)
            .beginFinally()
            .beginIf("fis != null")
            .beginTry()
            .addStatement("fis.close()")
            .beginCatch("$T e", IOException.class)
            .addStatement("throw new $T(e)", UncheckedIOException.class)
            .endTry()
            .endIf()
            .endTry()
            .end()
            .build();

        MethodSpec method5 = MethodSpec.builder(ArrayTypeName.ofTypeName(BYTE), "readFile", Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(File.class, "file")
            .beginMethodBody()
            .beginTry("$T fileInputStream = new $T((file))", FileInputStream.class, FileInputStream.class)
            .addStatement("byte[] b = new byte[$T.parseInt($T.valueOf(file.length()))]", Integer.class, String.class)
            .addStatement("int result = fileInputStream.read(b)")
            .beginIf("result == -1")
            .addStatement("throw new $T($S)", RuntimeException.class, "Read no data")
            .endIf()
            .addStatement("return b")
            .beginCatch("$T e", IOException.class)
            .addStatement("throw new $T(e)", UncheckedIOException.class)
            .endTry()
            .end()
            .build();

        MethodSpec method6 = MethodSpec.builder(INT, "sum1", Modifier.PRIVATE)
            .addParameter(INT, "threshold")
            .beginMethodBody()
            .addStatement("int result = 0")
            .beginFor("int i = 0; i < threshold; i++")
            .addStatement("result = result + i")
            .endFor()
            .addStatement("return result")
            .end()
            .build();

        MethodSpec method66 = MethodSpec.builder(INT, "sum1", Modifier.PRIVATE)
            .addParameter(INT, "a")
            .addParameter(INT, "b")
            .beginMethodBody()
            .addStatement("int result = 0")
            .beginFor("int i = 0; i < a; i++")
            .beginFor("int j = 0; j < b; j++")
            .addComment("compute i * j")
            .addStatement("result = result + i * j")
            .endFor()
            .endFor()
            .addStatement("return result")
            .end()
            .build();

        MethodSpec method7 = MethodSpec.builder(INT, "sum2", Modifier.PROTECTED, Modifier.STATIC)
            .addParameter(INT, "threshold")
            .beginMethodBody()
            .addStatement("int result = 0")
            .addStatement("int i = 0")
            .beginDo("i < threshold")
            .addStatement("result = result + ++i")
            .endDo()
            .addStatement("return result")
            .end()
            .build();

        MethodSpec method8 = MethodSpec.builder(INT, "sum3", Modifier.PROTECTED, Modifier.STATIC)
            .addParameter(INT, "threshold")
            .beginMethodBody()
            .addStatement("int result = 0")
            .addStatement("int i = 0")
            .beginWhile("i < threshold")
            .addStatement("result = result + ++i")
            .endWhile()
            .addStatement("return result")
            .end()
            .build();

        MethodSpec method9 = MethodSpec.builder(VOID, "act1", Modifier.PRIVATE, Modifier.STATIC)
            .addParameter(String.class, "path")
            .addParameter(Integer.class, "count")
            .addParameter(ArrayTypeName.ofType(String.class), "names")
            .beginMethodBody()
            .addStatement("System.out.println($S)", "HelloWorld")
            .end()
            .build();

        MethodSpec method10 = MethodSpec.builder(VOID, "act2", Modifier.PRIVATE, Modifier.STATIC)
            .addParameter(String.class, "path")
            .addParameter(ArrayTypeName.ofType(String.class), "names")
            .varargs()
            .beginMethodBody()
            .addStatement("System.out.println($S)", "HelloWorld")
            .end()
            .build();


        TypeVariableName k = TypeVariableName.of("K");
        ParameterizedTypeName p = ParameterizedTypeName.of(Comparable.class, WildcardTypeName.ofUpper(TypeVariableName.of("V")));
        TypeVariableName v = TypeVariableName.of("V", p);
        TypeVariableName m = TypeVariableName.of("M", ParameterizedTypeName.of(ClassName.ofClass(List.class), k), ClassName.ofClass(Serializable.class));

        TypeSpec innerType1 = ClassSpec.builder("Chair")
            .addField(String.class, "a1", Modifier.PRIVATE, Modifier.FINAL)
            .addConstructor(ConstructorSpec.builder(Modifier.PUBLIC)
                .addParameter(String.class, "a1")
                .beginConstructorBody()
                .addStatement("this.a1 = a1")
                .end()
                .build())
            .addMethod(MethodSpec.builder(String.class, "getName", Modifier.PUBLIC)
                .beginMethodBody()
                .addStatement("return this.a1")
                .end()
                .build())
            .build();

        TypeSpec innerType2 = ClassSpec.builder("Desk")
            .addField(INT, "a1", Modifier.PRIVATE, Modifier.FINAL)
            .addField(INT, "a2", Modifier.PRIVATE, Modifier.FINAL)
            .addConstructor(ConstructorSpec.builder(Modifier.PUBLIC)
                .addParameter(INT, "a1")
                .addParameter(INT, "a2")
                .beginConstructorBody()
                .addStatement("this.a1 = a1")
                .addStatement("this.a2 = a2")
                .end().build())
            .addMethod(MethodSpec.builder(INT, "sum", Modifier.PUBLIC)
                .beginMethodBody()
                .addStatement("return a1 + a2")
                .end().build())
            .build();

        ClassSpec classSpec = ClassSpec.builder("HelloClass")
            .setJavadoc(TypeJavadoc.builder()
                .beginJavadoc()
                .addDoc("创建历史书")
                .end()
                .build())
            .addAnnotation(annotationSpec1)
            .addAnnotation(annotationSpec2)
            .addAnnotation(annotationSpec3)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addTypeVariable(k)
            .addTypeVariable(v)
            .addTypeVariable(m)
            .addSuperInterface(Summary.class)
            .addSuperInterface(Ordered.class)
            .setSuperClass(ClassName.ofClass(State.class))
            .addField(field1)
            .addField(field2)
            .addField(field3)
            .addField(Worker.class, "workAaa", Modifier.PUBLIC)
            .addField(glz.hawk.codepoet.java.support.aab.Worker.class, "workAab", Modifier.PROTECTED)
            .addField(HelloClass.class, "helloClass", Modifier.PRIVATE, Modifier.STATIC)
            .addField(BOOLEAN, "flag", Modifier.PUBLIC)
            .addField(k, "k")
            .addField(v, "v")
            .addField(m, "m")
            .beginStaticInitializer()
            .addComment("HelloWorld1")
            .addComment("HelloWorld2")
            .end()
            .beginInstanceInitializer()
            .addComment("HelloWorld3")
            .addComment("HelloWorld4")
            .end()
            .addConstructor(constructor1)
            .addConstructor(constructor2)
            .addMethod(method1)
            .addMethod(method2)
            .addMethod(method3)
            .addMethod(method4)
            .addMethod(method5)
            .addMethod(method6)
            .addMethod(method66)
            .addMethod(method7)
            .addMethod(method8)
            .addMethod(method9)
            .addMethod(method10)
            .addInnerType(innerType1, Modifier.PUBLIC, Modifier.STATIC)
            .addInnerType(innerType2, Modifier.PRIVATE)
            .build();

        JavaFile javaFile = JavaFile.builder("com.example.test", classSpec)
            .setJavadoc(FileJavadoc.builder()
                .beginJavadoc()
                .addDocument("File Comment One.")
                .addDocument("File Comment Two.")
                .newLine()
                .end()
                .build())
            .build();

        javaFile.writeTo(System.out);
        javaFile.writeTo(javaFile.delete(srcTestJava()));
    }

}
