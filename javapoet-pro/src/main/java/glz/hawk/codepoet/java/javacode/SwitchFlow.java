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
public class SwitchFlow<P extends AbstractControlFlow<P>> extends AbstractSubControlFlow<SwitchFlow<P>, P> {
    public SwitchFlow(JavaCodeBlock.Builder codeBlockBuilder, P parent, String format, Object... args) {
        super(codeBlockBuilder, parent);
        //begin switch
        codeBlockBuilder.add(String.format("switch (%s) {", format), args).indent().newLine();
    }

    public CaseFlow<P> beginCase(String format, Object... args) {
        return new CaseFlow<>(codeBlockBuilder, this, format, args);
    }

    public DefaultFlow<P> beginDefault() {
        return new DefaultFlow<>(codeBlockBuilder, this);
    }

    public P endSwitch() {
        codeBlockBuilder.unindent().add("}").newLine();
        return parent;
    }

    public static class CaseFlow<P extends AbstractControlFlow<P>> extends AbstractSubControlFlow<CaseFlow<P>, SwitchFlow<P>> {

        public CaseFlow(JavaCodeBlock.Builder codeBlockBuilder, SwitchFlow<P> parent, String format, Object... args) {
            super(codeBlockBuilder, parent);
            beginCase(format, args);
        }

        private void beginCase(String format, Object... args) {
            codeBlockBuilder.add(String.format("case %s:", format), args).indent().newLine();
        }

        public SwitchFlow<P> breakCase() {
            addStatement("break");
            return endCase();
        }

        public SwitchFlow<P> endCase() {
            codeBlockBuilder.unindent();
            return parent;
        }
    }

    public static class DefaultFlow<P extends AbstractControlFlow<P>> extends AbstractSubControlFlow<DefaultFlow<P>, SwitchFlow<P>> {
        public DefaultFlow(JavaCodeBlock.Builder codeBlockBuilder, SwitchFlow<P> parent) {
            super(codeBlockBuilder, parent);
            //begin default
            codeBlockBuilder.add("default:").indent().newLine();
        }

        public P endSwitch() {
            codeBlockBuilder.unindent();
            return parent.endSwitch();
        }
    }
}
