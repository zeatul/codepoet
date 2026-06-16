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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class XmlFile {
    public final String packageName;
    private final Logger LOG = LoggerFactory.getLogger(XmlFile.class);
    private final String indent;
    private final String lineSeparator;
    private final List<Element> originatingElements;
    private final String filename;

    private XmlFile(Builder builder) {
        this.indent = builder.indent;
        this.lineSeparator = builder.lineSeparator;
        this.packageName = builder.packageName;
        this.originatingElements = builder.originatingElements;
        this.filename = builder.filename;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void writeTo(Filer filer, Consumer<PrettyXMLStreamWriter> consumer) {
        FileObject filerSourceFile = null;
        Writer writer = null;
        try {
            argNotBlank(packageName, packageName);
            argNotBlank(filename, "filename");
            filerSourceFile = filer.createResource(StandardLocation.CLASS_OUTPUT, packageName, filename, this.originatingElements.toArray(new Element[0]));
            writer = filerSourceFile.openWriter();
            consumer.accept(new PrettyXMLStreamWriter(writer));
        } catch (Exception e) {
            if (filerSourceFile != null) {
                try {
                    filerSourceFile.delete();
                } catch (Exception ignored) {
                    LOG.error("Failed to deleter filerSourceFile", e);
                }
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOG.error("Failed to close writer", e);
                }
            }
        }
    }

    public static final class Builder {
        private final List<Element> originatingElements = new ArrayList<>();
        private String indent = StringHelper.repeatChar(' ', 4);
        private String lineSeparator = "\n";
        private String packageName;
        private String filename;

        private Builder() {

        }

        public Builder setIndent(String indent) {
            this.indent = argNotNull(indent, "indent");
            return this;
        }

        public Builder setLineSeparator(String lineSeparator) {
            this.lineSeparator = argNotNull(lineSeparator, "lineSeparator");
            return this;
        }

        public Builder setPackage(String packageName) {
            this.packageName = argNotBlank(packageName, "packageName");
            return this;
        }

        public Builder addOriginatingElement(Element originatingElement) {
            this.originatingElements.add(argNotNull(originatingElement, "originatingElement"));
            return this;
        }

        public Builder addOriginatingElements(Element... originatingElements) {
            addOriginatingElements(Arrays.asList(originatingElements));
            return this;
        }

        public Builder addOriginatingElements(Iterable<Element> originatingElements) {
            int index = -1;
            for (Element element : argNotNull(originatingElements, "originatingElements")) {
                this.originatingElements.add(argElementNotNull(element, ++index, "originatingElements"));
            }
            return this;
        }

        public Builder setFilename(String filename) {
            this.filename = argNotBlank(filename, "filename");
            return this;
        }

        public XmlFile build() {
            //TODO:校验
            return new XmlFile(this);
        }

    }
}
