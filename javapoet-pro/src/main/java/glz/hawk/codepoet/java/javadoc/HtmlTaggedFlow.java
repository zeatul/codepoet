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

import glz.hawk.codepoet.java.JavaCodeBlock;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class HtmlTaggedFlow<P extends AbstractDocFlow<P>> extends AbstractSubDocFlow<HtmlTaggedFlow<P>, P> {

    private final HtmlTag tag;
    private final boolean hasIndent;
    private final boolean newLineOnOpen;
    private final boolean newLineOnClose;

    public HtmlTaggedFlow(JavaCodeBlock.Builder codeBlockBuilder, P parent, HtmlTag tag, boolean newLineOnOpen, boolean newLineOnClose) {
        super(codeBlockBuilder, parent);
        this.tag = tag;
        this.hasIndent = tag.hasIndent;
        this.newLineOnOpen = newLineOnOpen;
        this.newLineOnClose = newLineOnClose;

        // open tag
        codeBlockBuilder.add("$L", tag.openTag);
        if (hasIndent) codeBlockBuilder.indent();
        if (newLineOnOpen) codeBlockBuilder.newLine();
    }

    public P closeTag() {
        if (hasIndent) codeBlockBuilder.unindent();
        codeBlockBuilder.add("$L", tag.closeTag);
        if (newLineOnClose) codeBlockBuilder.newLine();
        return parent;
    }


}
