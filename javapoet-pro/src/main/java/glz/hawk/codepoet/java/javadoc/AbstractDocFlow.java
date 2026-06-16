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

import java.util.Map;
import java.util.function.Consumer;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class AbstractDocFlow<C extends AbstractDocFlow<C>> {

    protected JavaCodeBlock.Builder codeBlockBuilder;

    public AbstractDocFlow(JavaCodeBlock.Builder codeBlockBuilder) {
        this.codeBlockBuilder = argNotNull(codeBlockBuilder, "codeBlockBuilder");
    }

    public C addDoc(Consumer<JavaCodeBlock.Builder> consumer) {
        consumer.accept(codeBlockBuilder);
        return current();
    }

    public C addDoc(String format, Object... args) {
        codeBlockBuilder.add(format, args);
        return current();
    }

    public C addNamedDoc(String format, Map<String, ?> args) {
        codeBlockBuilder.addNamed(format, args);
        return current();
    }

    public C addDoc(JavaCodeBlock codeBlock) {
        codeBlockBuilder.add(codeBlock);
        return current();
    }

    /**
     * add doc and new line.
     */
    public C addDocument(String format, Object... args) {
        codeBlockBuilder.add(format, args).newLine();
        return current();
    }

    /**
     * add doc and new line.
     */
    public C addDocument(String format, Map<String, ?> args) {
        codeBlockBuilder.addNamed(format, args);
        return current();
    }

    @SuppressWarnings("unchecked")
    public HtmlTaggedFlow<C> openHtml(HtmlTag htmlTag, boolean newLineOnOpen, boolean newLineOnClose) {
        return new HtmlTaggedFlow<>(codeBlockBuilder, (C) this, argNotNull(htmlTag, "htmlTag"), newLineOnOpen, newLineOnClose);
    }

    public C addInlineHtml(HtmlTag htmlTag,JavaCodeBlock codeBlock) {
        return openHtml(htmlTag, false, false).addDoc(codeBlock).closeTag();
    }

    public C addInlineHtml(HtmlTag htmlTag,String format,Object... args) {
        return openHtml(htmlTag, false, false).addDoc(format,args).closeTag();
    }

    public C addNamedInlineHtml(HtmlTag htmlTag,String format, Map<String, ?> args) {
        return openHtml(htmlTag, false, false).addNamedDoc(format,args).closeTag();
    }

    public HtmlTaggedFlow<C> openBlockHtml(HtmlTag htmlTag) {
        return openHtml(htmlTag, true, true);
    }

    public C addInlineTag(InlineTag inlineTag, JavaCodeBlock codeBlock) {
        codeBlockBuilder.add("$L ", argNotNull(inlineTag, "inlineTag").openTag);
        codeBlockBuilder.add(codeBlock);
        codeBlockBuilder.add("$L", inlineTag.closeTag);
        return current();
    }

    public C addNamedInlineTag(InlineTag inlineTag, String format, Map<String, ?> args) {
        codeBlockBuilder.add("$L ", argNotNull(inlineTag, "inlineTag").openTag);
        codeBlockBuilder.addNamed(format,args);
        codeBlockBuilder.add("$L", inlineTag.closeTag);
        return current();
    }

    public C addInlineTag(InlineTag inlineTag, String format, Object... args) {
        codeBlockBuilder.add("$L ", argNotNull(inlineTag, "inlineTag").openTag);
        codeBlockBuilder.add(format, args);
        codeBlockBuilder.add("$L", inlineTag.closeTag);
        return current();
    }

    /**
     * {@code {@code }} tag
     */
    public C code(String format, Object... args) {
        return addInlineTag(InlineTag.CODE, format, args);
    }

    /**
     * {@code {@link }} tag
     */
    public C link(String format, Object... args) {
        return addInlineTag(InlineTag.LINK, format, args);
    }

    /**
     * {@code {@linkplain }} tag
     */
    public C linkplain(String format, Object... args) {
        return addInlineTag(InlineTag.LINKPLAIN, format, args);
    }

    /**
     * {@code {@literal }} tag
     */
    public C literal(String format, Object... args) {
        return addInlineTag(InlineTag.LITERAL, format, args);
    }

    public C newLine() {
        codeBlockBuilder.newLine();
        return current();
    }


    @SuppressWarnings("unchecked")
    private C current() {
        return (C) this;
    }

    public JavaCodeBlock buildCodeBlock() {
        return codeBlockBuilder.build();
    }
}
