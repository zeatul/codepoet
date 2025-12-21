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

import static glz.hawkframework.core.support.ArgumentSupport.argElementNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawk.codepoet.java.TypeCategory.ANNOTATION;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class AnnotationSpec extends TypeSpec {
    private AnnotationSpec(AnnotationSpec.Builder builder) {
        super(builder);
    }

    public static AnnotationSpec.Builder builder(String name, Modifier... modifiers) {
        return new AnnotationSpec.Builder(name, modifiers);
    }

    public static class Builder extends AbstractBuilder<AnnotationSpec.Builder> {

        protected Builder(String name, Modifier... modifiers) {
            super(ANNOTATION, name, modifiers);
        }

        public AnnotationSpec.Builder addAttribute(AttributeSpec attribute) {
            this.attributes.add(argNotNull(attribute, "attribute"));
            return this;
        }

        public AnnotationSpec.Builder addAttributes(AttributeSpec... attributes) {
            return addAttributes(Arrays.asList(attributes));
        }

        public AnnotationSpec.Builder addAttributes(Iterable<AttributeSpec> attributes) {
            int index = -1;
            for (AttributeSpec attribute : argNotNull(attributes, "attributes")) {
                addAttribute(argElementNotNull(attribute, ++index, "attributes"));
            }
            return this;
        }

        public AnnotationSpec build() {
            return new AnnotationSpec(this);
        }
    }
}

