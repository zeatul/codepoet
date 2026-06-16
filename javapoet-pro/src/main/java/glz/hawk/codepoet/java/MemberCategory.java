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
public enum MemberCategory {

    FIELD(Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE,
            Modifier.STATIC,
            Modifier.FINAL,
            Modifier.VOLATILE)),
    METHOD(Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE,
            Modifier.STATIC,
            Modifier.FINAL, Modifier.ABSTRACT,
            Modifier.DEFAULT,
            Modifier.SYNCHRONIZED,
            Modifier.STRICTFP)),
    CONSTRUCTOR(Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE)),
    ATTRIBUTE(Collections.singletonList(Modifier.PUBLIC)),
    INNER_CLASS(Arrays.asList(Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE));

    public final Set<Modifier> legalMemberModifiers;

    MemberCategory(List<Modifier> legalMemberModifiers) {
        this.legalMemberModifiers = toUnmodifiedSet(legalMemberModifiers);
    }

    public boolean isValid(Modifier modifier) {
        return legalMemberModifiers.contains(argNotNull(modifier, "modifier"));
    }
}
