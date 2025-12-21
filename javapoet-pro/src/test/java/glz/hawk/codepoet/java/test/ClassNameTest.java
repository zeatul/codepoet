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

import glz.hawk.codepoet.java.type.ClassName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ClassNameTest {

    @Test
    public void ofClassTest() {
        ClassName className = ClassName.ofClass(List.class);
        assertThat(className.packageName()).isEqualTo(List.class.getPackage().getName());
        assertThat(className.simpleName()).isEqualTo(List.class.getSimpleName());
        assertThat(className.canonicalName()).isEqualTo(List.class.getCanonicalName());
        assertThat(className.encolsingClassName()).isNull();

        className = ClassName.ofClass(Map.Entry.class);
        assertThat(className.packageName()).isEqualTo(Map.class.getPackage().getName());
        assertThat(className.simpleName()).isEqualTo(Map.Entry.class.getSimpleName());
        assertThat(className.canonicalName()).isEqualTo(Map.Entry.class.getCanonicalName());
        assertThat(className.encolsingClassName().canonicalName()).isEqualTo(Map.class.getCanonicalName());
    }

    @Test
    public void ofTest() {
        String packageName = "java.util";
        String simpleName = "Map";
        ClassName className = ClassName.of(packageName, simpleName);
        assertThat(className.packageName()).isEqualTo(packageName);
        assertThat(className.simpleName()).isEqualTo(simpleName);
        assertThat(className.canonicalName()).isEqualTo(packageName + "." + simpleName);
        assertThat(className.encolsingClassName()).isNull();

        String[] simpleNames = new String[]{"Entry"};
        className = ClassName.of(packageName, simpleName, simpleNames);
        assertThat(className.packageName()).isEqualTo(packageName);
        assertThat(className.simpleName()).isEqualTo(simpleNames[0]);
        assertThat(className.canonicalName()).isEqualTo(packageName + "." + simpleName + "." + simpleNames[0]);
        assertThat(className.encolsingClassName().canonicalName()).isEqualTo(packageName + "." + simpleName);
    }

    @Test
    public void ofGuessTest() {
        String packageName = "java.util";
        String simpleName = "Map";
        String classNameString = packageName + "." + simpleName;
        ClassName className = ClassName.ofGuess(classNameString);
        assertThat(className.packageName()).isEqualTo(packageName);
        assertThat(className.simpleName()).isEqualTo(simpleName);
        assertThat(className.canonicalName()).isEqualTo(packageName + "." + simpleName);
        assertThat(className.encolsingClassName()).isNull();

        String[] simpleNames = new String[]{"Entry"};
        classNameString = packageName + "." + simpleName + "."+simpleNames[0];
        className = ClassName.ofGuess(classNameString);
        assertThat(className.packageName()).isEqualTo(packageName);
        assertThat(className.simpleName()).isEqualTo(simpleNames[0]);
        assertThat(className.canonicalName()).isEqualTo(packageName + "." + simpleName + "." + simpleNames[0]);
        assertThat(className.encolsingClassName().canonicalName()).isEqualTo(packageName + "." + simpleName);
    }
}
