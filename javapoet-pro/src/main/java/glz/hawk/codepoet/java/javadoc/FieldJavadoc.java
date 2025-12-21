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

package glz.hawk.codepoet.java.javadoc;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class FieldJavadoc extends Javadoc {

    protected FieldJavadoc(FieldJavadoc.Builder builder) {
        super(builder);
    }

    public static FieldJavadoc.Builder builder() {
        return new FieldJavadoc.Builder();
    }

    public static class Builder extends AbstractJavadocBuilder<FieldJavadoc.Builder> {

        private Builder() {
            super(Location.FIELD);
        }

        public FieldJavadoc build() {
            return new FieldJavadoc(this);
        }
    }
}
