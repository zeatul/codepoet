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

package glz.hawk.codepoet.core;

import java.io.IOException;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
@SuppressWarnings("ALL")
public abstract class AbstractCodeWriter<T extends AbstractCodeWriter<T>> {

    protected final Appendable out;
    protected final String indent;
    protected final String lineSeparator;
    protected int indentLevel = 0;
    /**
     * whether to trail a new line.
     * <p>If {@code trailingNewline} is {@code true}, the output position is at the start of a new line, the indent must be emitted.</p>
     */
    protected boolean trailingNewline = true;

    protected AbstractCodeWriter(Appendable out, String indent, String lineSeparator) {
        this.out = argNotNull(out, "out");
        this.indent = argNotNull(indent, "indent");
        this.lineSeparator = argNotNull(lineSeparator, "lineSeparator");
    }

    public T indent() {
        return indent(1);
    }

    public T indent(int level) {
        argument(level, l -> l > 0, l -> "level must be greater than 0.");
        this.indentLevel = indentLevel + level;
        return (T)this;
    }

    public T unindent() {
        return unindent(1);
    }

    public T unindent(int level) {
        argument(level, l -> l > 0, l -> "level must be greater than 0.");
        argument(level, l -> (indentLevel - l) >= 0, l -> "level must be less than or equals to indentLevel.");
        this.indentLevel = indentLevel - level;
        return (T)this;
    }

    public T emitNewLine() throws IOException {
        out.append(lineSeparator);
        trailingNewline = true;
        return (T)this;
    }

    public T emitAndIndent(String s) throws IOException {
        if (trailingNewline) {
            emitIndentation();
        }
        out.append(s);
        trailingNewline = false;
        return (T)this;
    }

    protected void emitIndentation() throws IOException {
        for (int i = 0; i < indentLevel; i++) {
            out.append(indent);
        }
    }



}
