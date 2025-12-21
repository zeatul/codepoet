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

package glz.hawk.codepoet.java.javacode;

import glz.hawk.codepoet.java.JavaCodeBlock;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class IfFlow<P extends AbstractControlFlow<P>> extends AbstractSubControlFlow<IfFlow<P>, P> {
    public IfFlow(JavaCodeBlock.Builder codeBlockBuilder, P parent, String format, Object... args) {
        super(codeBlockBuilder, parent);
        //begin if
        codeBlockBuilder.add(String.format("if (%s) {", format), args).indent().newLine();
    }

    public IfFlow<P> beginElseIf(String format, Object... args) {
        codeBlockBuilder.unindent().add(String.format("} else if (%s) {", format), args).indent().newLine();
        return this;
    }

    public ElseFlow<P> beginElse() {
        return new ElseFlow<>(codeBlockBuilder, this);
    }

    public P endIf() {
        codeBlockBuilder.unindent().add("}").newLine();
        return parent;
    }

    public static class ElseFlow<P extends AbstractControlFlow<P>> extends AbstractSubControlFlow<ElseFlow<P>, IfFlow<P>> {
        public ElseFlow(JavaCodeBlock.Builder codeBlockBuilder, IfFlow<P> parent) {
            super(codeBlockBuilder, parent);
            toElse();
        }

        private void toElse() {
            codeBlockBuilder.unindent().add("} else {").indent().newLine();
        }

        public P endIf() {
            return parent.endIf();
        }
    }
}
