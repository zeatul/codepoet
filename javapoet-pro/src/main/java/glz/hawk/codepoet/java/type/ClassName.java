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

package glz.hawk.codepoet.java.type;

import glz.hawkframework.core.helper.StringHelper;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ClassName implements TypeName {

    private static final String NO_PACKAGE = "";

    final String packageName;

    final ClassName encolsingClassName;

    final String simpleName;

    final String canonicalName;

    private ClassName(String packageName, ClassName enclosingClassName, String simpleName) {
        //TODO:校验packageName
        this.packageName = argNotNull(packageName, "packageName");
        this.encolsingClassName = enclosingClassName;
        //TODO:校验simpleName
        this.simpleName = argNotBlank(simpleName, "simpleName");
        this.canonicalName = enclosingClassName != null
            ? (enclosingClassName.canonicalName + '.' + simpleName)
            : (packageName.isEmpty() ? simpleName : packageName + '.' + simpleName);
    }

    public static ClassName ofClass(Class<?> clazz) {
        argNotNull(clazz, "clazz");
        argFalse(clazz::isPrimitive, () -> "Primitive type can't be used to construct ClassName. Please use PrimitiveTypeName to define primitive type");
        argFalse(clazz::isArray, () -> "Array type can't be used to construct ClassName.");
        argFalse(() -> clazz == Void.class, () -> "Void type can't be used to construct ClassName. Please use VoidTypeName to define void type");

        //TODO: Support Anonymous Class?
        argFalse(clazz::isAnonymousClass, () -> "Anonymous type can't be used to construct ClassName.");

        String simpleName = clazz.getSimpleName();
        if (clazz.getEnclosingClass() == null) {
            int lastDot = clazz.getName().lastIndexOf('.');
            String packageName = (lastDot != -1) ? clazz.getName().substring(0, lastDot) : NO_PACKAGE;
            return new ClassName(packageName, null, simpleName);
        }

        ClassName className = ofClass(clazz.getEnclosingClass());
        return new ClassName(className.packageName, className, simpleName);
    }

    /**
     * packageName = {@code "java.util"}, simpleName = {@code "Map}, simpleNames = {@code ["Entry"]} ==> className = {@code "java.util.Map.Entry"}
     */
    public static ClassName of(String packageName, String simpleName, String... simpleNames) {
        ClassName className = new ClassName(packageName, null, simpleName);
        for (String name : simpleNames) {
            className = new ClassName(className.packageName, className, name);
        }
        return className;
    }

    public static ClassName of(TypeElement typeElement) {
        argNotNull(typeElement, "typeElement");
        String simpleName = typeElement.getSimpleName().toString();
        return typeElement.getEnclosingElement().accept(new SimpleElementVisitor8<ClassName, Void>() {
            @Override
            public ClassName visitPackage(PackageElement packageElement, Void unused) {
                return ClassName.of(packageElement.getQualifiedName().toString(), simpleName);
            }

            @Override
            public ClassName visitType(TypeElement enclosingClass, Void unused) {
                ClassName className = ClassName.of(enclosingClass);
                return new ClassName(className.packageName(), className, simpleName);
            }

            @Override
            public ClassName visitUnknown(Element e, Void unused) {
                return new ClassName("", null, simpleName);
            }

            @Override
            protected ClassName defaultAction(Element e, Void unused) {
                throw new IllegalArgumentException("Unexpected type element: " + typeElement);
            }
        }, null);
    }

    /**
     * Form the first part started with an uppercase letter, all remaining parts are considered as {@code ClassName}.
     */
    public static ClassName ofGuess(String classNameString) {
        argNotBlank(classNameString, "classNameString");
        String[] array = classNameString.split("\\.");

        List<String> packageParts = new ArrayList<>();
        List<String> simpleNameParts = new ArrayList<>();
        boolean metUpperInitialBefore = false;
        for (int i = 0; i < array.length; i++) {
            String part = array[i];
            if (StringHelper.isBlank(part)) {
                throw new IllegalArgumentException(String.format("The parameter: %s is illegal", classNameString));
            }
            if (i == array.length - 1) {
                simpleNameParts.add(part);
                continue;
            }
            if (metUpperInitialBefore) {
                simpleNameParts.add(part);
                continue;
            }
            if (Character.isUpperCase(part.codePointAt(0))) {
                simpleNameParts.add(part);
                metUpperInitialBefore = true;
                continue;
            }
            packageParts.add(part);
        }
        String packageName = packageParts.isEmpty() ? NO_PACKAGE : String.join(".", packageParts);
        return of(packageName, simpleNameParts.get(0), simpleNameParts.subList(1, simpleNameParts.size()).toArray(new String[]{}));
    }


    public String packageName() {
        return this.packageName;
    }

    public String canonicalName() {
        return this.canonicalName;
    }

    public String simpleName() {
        return this.simpleName;
    }

    public ClassName encolsingClassName() {
        return this.encolsingClassName;
    }

    @Nonnull
    @Override
    public String keyword() {
        return AlwaysQualifiedNamesHolder.get().contains(simpleName) ? canonicalName : simpleName;
    }

    @Nonnull
    @Override
    public Set<String> imports() {
        return Collections.singleton(canonicalName);
    }
}
