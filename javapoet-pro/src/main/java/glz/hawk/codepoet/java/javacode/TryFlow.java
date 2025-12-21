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
public class TryFlow<P extends AbstractControlFlow<P>> extends AbstractSubControlFlow<TryFlow<P>, P> {
    public TryFlow(JavaCodeBlock.Builder codeBlockBuilder, P parent, String format, Object... args) {
        super(codeBlockBuilder, parent);
        // begin try resource
        codeBlockBuilder.add(String.format("try (%s) {", format), args).indent().newLine();
    }

    public TryFlow(JavaCodeBlock.Builder codeBlockBuilder, P parent) {
        super(codeBlockBuilder, parent);
        // begin try
        codeBlockBuilder.add("try {").indent().newLine();
    }

    public TryFlow<P> beginCatch(String format, Object... args) {
        codeBlockBuilder.unindent().add(String.format("} catch (%s) {", format), args).indent().newLine();
        return this;
    }

    public FinallyFlow<P> beginFinally() {
        return new FinallyFlow<>(codeBlockBuilder, this);
    }

    public P endTry() {
        codeBlockBuilder.unindent().add("}").newLine();
        return parent;
    }

    public static class FinallyFlow<P extends AbstractControlFlow<P>> extends AbstractSubControlFlow<FinallyFlow<P>, TryFlow<P>> {
        public FinallyFlow(JavaCodeBlock.Builder codeBlockBuilder, TryFlow<P> parent) {
            super(codeBlockBuilder, parent);
            beginFinally();
        }

        private void beginFinally(){
            codeBlockBuilder.unindent().add("} finally {").indent().newLine();
        }

        public P endTry() {
            return parent.endTry();
        }

    }

}
