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

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static glz.hawk.codepoet.java.ModifierSupport.toUnmodifiedSet;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public enum TypeCategory {
    CLASS("class",
            Arrays.asList(Modifier.PUBLIC,
                    Modifier.ABSTRACT, Modifier.FINAL,
                    Modifier.STRICTFP),
            Collections.emptyList(),
            Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE,
                    Modifier.ABSTRACT, Modifier.FINAL, Modifier.STATIC,
                    Modifier.STRICTFP,
                    Modifier.SYNCHRONIZED),
            Collections.emptyList(),
            Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE),
            Collections.emptyList(),
            Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE,
                    Modifier.VOLATILE,
                    Modifier.FINAL,
                    Modifier.STATIC),
            Collections.emptyList(),
            Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE, Modifier.STATIC),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()),

    INTERFACE("interface",
            Arrays.asList(Modifier.PUBLIC,
                    Modifier.ABSTRACT,
                    Modifier.STRICTFP),
            Collections.singletonList(Modifier.ABSTRACT),
            Arrays.asList(Modifier.PUBLIC,
                    Modifier.STATIC, Modifier.DEFAULT, Modifier.ABSTRACT,
                    Modifier.STRICTFP),
            Collections.singletonList(Modifier.ABSTRACT),
            Collections.emptyList(),
            Collections.emptyList(),
            Arrays.asList(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
            Arrays.asList(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
            Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE, Modifier.STATIC),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()),

    ENUM("enum",
            Arrays.asList(Modifier.PUBLIC, Modifier.STRICTFP),
            Collections.emptyList(),
            Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE,
                    Modifier.FINAL, Modifier.STATIC, Modifier.ABSTRACT,
                    Modifier.STRICTFP,
                    Modifier.SYNCHRONIZED),
            Collections.singletonList(Modifier.FINAL),
            Collections.singletonList(Modifier.PRIVATE),
            Collections.singletonList(Modifier.PRIVATE),
            Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE,
                    Modifier.VOLATILE,
                    Modifier.FINAL,
                    Modifier.STATIC),
            Collections.emptyList(),
            Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE, Modifier.STATIC),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()),

    ANNOTATION("@interface",
            Arrays.asList(Modifier.PUBLIC, Modifier.STRICTFP, Modifier.ABSTRACT),
            Collections.singletonList(Modifier.ABSTRACT),
            Arrays.asList(Modifier.PUBLIC, Modifier.ABSTRACT),
            Arrays.asList(Modifier.PUBLIC, Modifier.ABSTRACT),
            Collections.emptyList(),
            Collections.emptyList(),
            Arrays.asList(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
            Arrays.asList(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
            Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE, Modifier.STATIC),
            Collections.emptyList(),
            Collections.singletonList(Modifier.PUBLIC),
            Collections.singletonList(Modifier.PUBLIC)),

    RECORD("record",
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE, Modifier.STATIC),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList());

    public final String name;

    public final Set<Modifier> legalTypeModifiers;
    public final Set<Modifier> implicitTypeModifiers;
    public final Set<Modifier> legalMethodModifiers;
    public final Set<Modifier> implicitMethodModifiers;
    public final Set<Modifier> legalConstructorModifiers;
    public final Set<Modifier> implicitConstructorModifiers;
    public final Set<Modifier> legalFieldModifiers;
    public final Set<Modifier> implicitFieldModifiers;
    public final Set<Modifier> legalAsMemberModifiers;
    public final Set<Modifier> implicitAsMemberModifiers;
    public final Set<Modifier> legalAttributeModifiers;
    public final Set<Modifier> implicitAttributeModifiers;

    TypeCategory(String name, List<Modifier> legalTypeModifiers
            , List<Modifier> implicitTypeModifiers
            , List<Modifier> legalMethodModifiers
            , List<Modifier> implicitMethodModifiers
            , List<Modifier> legalConstructorModifiers
            , List<Modifier> implicitConstructorModifiers
            , List<Modifier> legalFieldModifiers
            , List<Modifier> implicitFieldModifiers
            , List<Modifier> legalAsMemberModifiers
            , List<Modifier> implicitAsMemberModifiers
            , List<Modifier> legalAttributeModifiers
            , List<Modifier> implicitAttributeModifiers) {
        this.name = name;
        this.legalTypeModifiers = toUnmodifiedSet(legalTypeModifiers);
        this.implicitTypeModifiers = toUnmodifiedSet(implicitTypeModifiers);
        this.legalMethodModifiers = toUnmodifiedSet(legalMethodModifiers);
        this.implicitMethodModifiers = toUnmodifiedSet(implicitMethodModifiers);
        this.legalConstructorModifiers = toUnmodifiedSet(legalConstructorModifiers);
        this.implicitConstructorModifiers = toUnmodifiedSet(implicitConstructorModifiers);
        this.legalFieldModifiers = toUnmodifiedSet(legalFieldModifiers);
        this.implicitFieldModifiers = toUnmodifiedSet(implicitFieldModifiers);
        this.legalAsMemberModifiers = toUnmodifiedSet(legalAsMemberModifiers);
        this.implicitAsMemberModifiers = toUnmodifiedSet(implicitAsMemberModifiers);
        this.legalAttributeModifiers = toUnmodifiedSet(legalAttributeModifiers);
        this.implicitAttributeModifiers = toUnmodifiedSet(implicitAttributeModifiers);
    }


    public boolean isValidForType(Modifier modifier) {
        return legalTypeModifiers.contains(argNotNull(modifier, "modifier"));
    }

    public boolean isValidForAsMember(Modifier modifier) {
        return legalAsMemberModifiers.contains(argNotNull(modifier, "modifier"));
    }


    @Override
    public String toString() {
        return this.name;
    }
}
