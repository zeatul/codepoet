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

package glz.hawk.codepoet.xml;

import glz.hawkframework.core.helper.StringHelper;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class PrettyXMLStreamWriter implements XMLStreamWriter {
    private final XMLStreamWriter delegate;
    private final String indent = "    ";
    private final String lineSeparator = "\n";
    // 栈中每项表示对应元素是否已写出子内容（child element 或 非空文本）
    private final Deque<Boolean> hasChild = new ArrayDeque<>();
    /**
     * whether to trail a new line.
     * <p>If {@code trailingNewline} is {@code true}, the output position is at the start of a new line, the indent must be emitted.</p>
     */
    protected boolean trailingNewline = true;
    private int depth = 0;
    private boolean hasCalledStartDocument = false;
    private boolean isFirstElement = true;

    public PrettyXMLStreamWriter(Writer writer) {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            delegate = factory.createXMLStreamWriter(writer);
        } catch (XMLStreamException e) {
            throw new XmlPoetException(e);
        }
    }

    @Override
    public void writeStartDocument() {
        exec(delegate::<String>writeStartDocument);
        hasCalledStartDocument = true;
    }

    @Override
    public void writeStartDocument(String version) {
        exec(delegate::<String>writeStartDocument, version);
        hasCalledStartDocument = true;
    }

    @Override
    public void writeStartDocument(String encoding, String version) {
        exec(delegate::<String, String>writeStartDocument, encoding, version);
        hasCalledStartDocument = true;
    }

    @Override
    public void writeStartElement(String localName) {
        // 调用了writeStartDocument，可以直接加换行符。
        // 如果没有调用调用了writeStartDocument，xml文档的第一个元素前不可以加换行符。
        if (hasCalledStartDocument || !isFirstElement) {
            writeNewLineAndIndent();
        }
        exec(delegate::writeStartElement, localName);
        markParentHasChild();
        depth++;
        hasChild.push(false);
        isFirstElement = false;
    }


    @Override
    public void writeStartElement(String namespaceURI, String localName) {
        if (hasCalledStartDocument || !isFirstElement) {
            writeNewLineAndIndent();
        }
        exec(delegate::writeStartElement, namespaceURI, localName);
        markParentHasChild();
        depth++;
        hasChild.push(false);
        isFirstElement = false;
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) {
        if (hasCalledStartDocument || !isFirstElement) {
            writeNewLineAndIndent();
        }
        exec(delegate::writeStartElement, prefix, localName, namespaceURI);
        markParentHasChild();
        depth++;
        hasChild.push(false);
        isFirstElement = false;
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) {
        if (hasCalledStartDocument || !isFirstElement) {
            writeNewLineAndIndent();
        }
        exec(delegate::writeEmptyElement, namespaceURI, localName);
        markParentHasChild();
        isFirstElement = false;
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) {
        if (hasCalledStartDocument || !isFirstElement) {
            writeNewLineAndIndent();
        }
        exec(delegate::writeEmptyElement, prefix, localName, namespaceURI);
        markParentHasChild();
        isFirstElement = false;
    }

    @Override
    public void writeEmptyElement(String localName) {
        if (hasCalledStartDocument || !isFirstElement) {
            writeNewLineAndIndent();
        }
        exec(delegate::writeEmptyElement, localName);
        markParentHasChild();
        isFirstElement = false;
    }

    @Override
    public void writeEndElement() {
        depth--;
        if (hasChild.pop()) {
            writeNewLineAndIndent();
        }
        exec(delegate::writeEndElement);

    }

    @Override
    public void writeEndDocument() {
        exec(delegate::writeEndDocument);
    }

    @Override
    public void close() {
        exec(delegate::close);
    }

    @Override
    public void flush() {
        exec(delegate::flush);
    }

    @Override
    public void writeAttribute(String localName, String value) {
        exec(delegate::writeAttribute, localName, value);
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) {
        exec(delegate::writeAttribute, prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) {
        exec(delegate::writeAttribute, namespaceURI, localName, value);
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) {
        exec(delegate::writeNamespace, prefix, namespaceURI);
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) {
        exec(delegate::writeDefaultNamespace, namespaceURI);
    }

    @Override
    public void writeComment(String data) {
        exec(delegate::writeComment, data);
    }

    @Override
    public void writeProcessingInstruction(String target) {
        exec(delegate::writeProcessingInstruction, target);
    }

    @Override
    public void writeProcessingInstruction(String target, String data) {
        exec(delegate::writeProcessingInstruction, target, data);
    }

    @Override
    public void writeCData(String data) {
        exec(delegate::writeCData, data);
    }

    /**
     * 将data的值放在新行
     */

    public void writeCData(List<String> dataList) {
        StringBuilder sb = new StringBuilder();
        sb.append(lineSeparator);
        dataList.forEach(data ->
            sb.append(StringHelper.repeatStr(indent, depth))
                .append(data)
                .append(lineSeparator)
        );
        sb.append(StringHelper.repeatStr(indent, depth - 1));
        exec(delegate::writeCData, sb.toString());
    }

    @Override
    public void writeDTD(String dtd) {
        exec(delegate::writeDTD, dtd);
    }

    @Override
    public void writeEntityRef(String name) {
        exec(delegate::writeEntityRef, name);
    }

    @Override
    public void writeCharacters(String text) {
        exec(delegate::writeCharacters, text);
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) {
        exec(delegate::writeCharacters, text, start, len);
    }

    @Override
    public String getPrefix(String uri) {
        try {
            return delegate.getPrefix(uri);
        } catch (XMLStreamException e) {
            throw new XmlPoetException(e);
        }
    }

    @Override
    public void setPrefix(String prefix, String uri) {
        exec(delegate::setPrefix, prefix, uri);
    }

    @Override
    public void setDefaultNamespace(String uri) {
        exec(delegate::setDefaultNamespace, uri);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return delegate.getNamespaceContext();
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) {
        exec(delegate::setNamespaceContext, context);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return delegate.getProperty(name);
    }

    private void exec(F0 f) {
        try {
            f.accept();
        } catch (XMLStreamException e) {
            throw new XmlPoetException(e);
        }
    }

    private <K> void exec(F1<K> f, K arg1) {
        try {
            f.accept(arg1);
        } catch (XMLStreamException e) {
            throw new XmlPoetException(e);
        }
    }

    private <K, L> void exec(F2<K, L> f, K arg1, L arg2) {
        try {
            f.accept(arg1, arg2);
        } catch (XMLStreamException e) {
            throw new XmlPoetException(e);
        }
    }

    private <K, L, S> void exec(F3<K, L, S> f, K arg1, L arg2, S arg3) {
        try {
            f.accept(arg1, arg2, arg3);
        } catch (XMLStreamException e) {
            throw new XmlPoetException(e);
        }
    }

    private <K, L, S, M> void exec(F4<K, L, S, M> f, K arg1, L arg2, S arg3, M arg4) {
        try {
            f.accept(arg1, arg2, arg3, arg4);
        } catch (XMLStreamException e) {
            throw new XmlPoetException(e);
        }
    }

    private void writeNewLineAndIndent() {
        exec(delegate::writeCharacters, lineSeparator);
        for (int i = 0; i < depth; i++) {
            exec(delegate::writeCharacters, indent);
        }
    }

    private void markParentHasChild() {
        if (!hasChild.isEmpty()) {
            hasChild.pop();
            // mark parent has child
            hasChild.push(true);
        }
    }

    public int getDepth() {
        return this.depth;
    }

    public String getIndent() {
        return this.indent;
    }

    public String getLineSeparator() {
        return this.lineSeparator;
    }

    private static interface F0 {
        public void accept() throws XMLStreamException;
    }

    private static interface F1<K> {
        public void accept(K arg1) throws XMLStreamException;
    }

    private static interface F2<K, S> {
        public void accept(K arg1, S arg2) throws XMLStreamException;
    }

    private static interface F3<K, S, L> {
        public void accept(K arg1, S arg2, L arg3) throws XMLStreamException;
    }

    private static interface F4<K, S, L, M> {
        public void accept(K arg1, S arg2, L arg3, M arg4) throws XMLStreamException;
    }
}
