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

import java.util.Map;
import java.util.function.Consumer;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * This class is responsible for providing basic java and java control flow.
 *
 * @author Hawk
 */
@SuppressWarnings("unchecked")
public abstract class AbstractControlFlow<C extends AbstractControlFlow<C>> {
    protected JavaCodeBlock.Builder codeBlockBuilder;


    public AbstractControlFlow(JavaCodeBlock.Builder codeBlockBuilder) {
        this.codeBlockBuilder = argNotNull(codeBlockBuilder, "codeBlockBuilder");
    }

    public C addCode(String format, Object... args) {
        codeBlockBuilder.add(format, args);
        return current();
    }

    public C addCode(Consumer<C> consumer) {
        consumer.accept(current());
        return current();
    }

    public C addNamedCode(String format, Map<String, ?> args) {
        codeBlockBuilder.addNamed(format, args);
        return current();
    }

    public C addCode(JavaCodeBlock codeBlock) {
        codeBlockBuilder.add(codeBlock);
        return current();
    }

    /**
     * {@code addStatement} will add {@code ;} and {@code line separator} automatically.
     */
    public C addStatement(String format, Object... args) {
        codeBlockBuilder.add(format, args).add(";").newLine();
        return current();
    }

    /**
     * {@code addStatement} will add {@code ;} and {@code line separator} automatically.
     */
    public C addStatement(String format, Map<String, ?> args) {
        codeBlockBuilder.addNamed(format, args).add(";").newLine();
        return current();
    }

    public C addNewLine() {
        codeBlockBuilder.newLine();
        return current();
    }

    public C addIndent() {
        codeBlockBuilder.indent();
        return current();
    }

    public C addIndent(int level) {
        argument(level, l -> l > 0, l -> "level must be greater than 0.");
        for (int i = 0; i < level; i++) {
            codeBlockBuilder.indent();
        }
        return current();
    }

    public C removeIndent() {
        codeBlockBuilder.unindent();
        return current();
    }

    public C removeIndent(int level) {
        argument(level, l -> l > 0, l -> "level must be greater than 0.");
        for (int i = 0; i < level; i++) {
            codeBlockBuilder.unindent();
        }
        return current();
    }

    /**
     * {@code addComment} will add {@code //} and {@code line separator} automatically.
     */
    public C addComment(String format, Object... args) {
        codeBlockBuilder.add("// " + format, args).newLine();
        return current();
    }

    private C current() {
        return (C) this;
    }

    public ForFlow<C> beginFor(String format, Object... args) {
        return new ForFlow<>(codeBlockBuilder, (C) this, format, args);
    }

    public WhileFlow<C> beginWhile(String format, Object... args) {
        return new WhileFlow<>(codeBlockBuilder, (C) this, format, args);
    }

    public DoFlow<C> beginDo(String format, Object... args) {
        return new DoFlow<>(codeBlockBuilder, (C) this, format, args);
    }

    public BracketFlow<C> openBracket(JavaCodeBlock.Builder codeBlockBuilder, BracketFlow.Bracket bracket, boolean hasIndent, boolean newLineOnOpen, boolean newLineOnClose) {
        return new BracketFlow<>(codeBlockBuilder, (C) this, bracket, hasIndent, newLineOnOpen, newLineOnClose);
    }

    public BracketFlow<C> openCurlyBracket(JavaCodeBlock.Builder codeBlockBuilder) {
        return new BracketFlow<>(codeBlockBuilder, (C) this, BracketFlow.Bracket.CURLY, true, true, true);
    }

    public BracketFlow<C> openRoundBracket(JavaCodeBlock.Builder codeBlockBuilder) {
        return new BracketFlow<>(codeBlockBuilder, (C) this, BracketFlow.Bracket.ROUND, false, false, false);
    }

    public IfFlow<C> beginIf(String format, Object... args) {
        return new IfFlow<>(codeBlockBuilder, (C) this, format, args);
    }

    public TryFlow<C> beginTry(String format, Object... args) {
        return new TryFlow<>(codeBlockBuilder, (C) this, format, args);
    }

    public TryFlow<C> beginTry() {
        return new TryFlow<>(codeBlockBuilder, (C) this);
    }

    public SwitchFlow<C> beginSwitch(String format, Object... args) {
        return new SwitchFlow<>(codeBlockBuilder, (C) this, format, args);
    }

    public JavaCodeBlock buildCodeBlock() {
        return codeBlockBuilder.build();
    }

}
