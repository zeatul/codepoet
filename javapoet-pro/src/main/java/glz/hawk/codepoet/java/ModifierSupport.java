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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class ModifierSupport {

    private final static List<Set<Modifier>> mutualExclusions = Arrays.asList(
            new HashSet<>(Arrays.asList(Modifier.FINAL, Modifier.ABSTRACT)),
            new HashSet<>(Arrays.asList(Modifier.FINAL, Modifier.VOLATILE)),
            new HashSet<>(Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE)),
            new HashSet<>(Arrays.asList(Modifier.STATIC, Modifier.DEFAULT, Modifier.ABSTRACT))
    );

    private final static Map<Modifier, Set<Modifier>> implicitMethodMap = new HashMap<>();

    static {
        implicitMethodMap.put(Modifier.STATIC, toUnmodifiedSet(Collections.singletonList(Modifier.FINAL)));
        implicitMethodMap.put(Modifier.PRIVATE, toUnmodifiedSet(Collections.singletonList(Modifier.FINAL)));
    }

    public static Set<Modifier> toUnmodifiedSet(List<Modifier> modifiers) {
        return Collections.unmodifiableSet(new LinkedHashSet<>(modifiers));
    }

    public static Set<Modifier> toLinkedHashSet(List<Modifier> modifiers) {
        return new LinkedHashSet<>(modifiers);
    }

    public static Set<Modifier> toHashSet(List<Modifier> modifiers) {
        return new HashSet<>(modifiers);
    }

    public static Set<Modifier> dynamicImplicitMethodModifiers(Set<Modifier> modifiers) {
        if (modifiers == null) {
            return Collections.emptySet();
        }
        Set<Modifier> set = new HashSet<>();
        modifiers.stream().map(m -> implicitMethodMap.getOrDefault(m, Collections.emptySet())).forEach(set::addAll);
        return set;
    }

    @Nonnull
    public static List<Set<Modifier>> getMutualExclusions(@Nullable Modifier modifier) {
        if (modifier == null) {
            return Collections.emptyList();
        }

        List<Set<Modifier>> result = new ArrayList<>();
        for (Set<Modifier> set : mutualExclusions) {
            if (set.contains(modifier)) {
                result.add(set);
            }
        }

        return result;
    }

    public static Set<Modifier> legalParameterModifiers() {
        return new HashSet<>(Collections.singletonList(Modifier.FINAL));
    }

    public static Set<Modifier> implicitParameterModifiers() {
        return Collections.emptySet();
    }

}
