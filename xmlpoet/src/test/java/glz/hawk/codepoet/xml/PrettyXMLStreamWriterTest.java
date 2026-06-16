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

import glz.hawkframework.core.helper.FileReadHelper;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class PrettyXMLStreamWriterTest {

    @Test
    public void test1() {
        StringWriter sw = new StringWriter();
        PrettyXMLStreamWriter writer = new PrettyXMLStreamWriter(sw);
        writer.writeStartDocument();
        writer.writeStartElement("Project");
        writer.writeAttribute("language", "java");
        writer.writeStartElement("Developer");
        writer.writeAttribute("id", "101010");
        writer.writeStartElement("name");
        writer.writeCData(Arrays.asList("Hawk<>12", "Hawk13", "Hawk14"));
        writer.writeEndElement();
        writer.writeStartElement("email");
        writer.writeCharacters("fenris@hotmail.com");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
        assertThat(sw.toString()).isEqualTo(FileReadHelper.readAllText("/glz/hawk/codepoet/xml/test1.xml").trim());
    }

    @Test
    public void test2() {
        StringWriter sw = new StringWriter();
        PrettyXMLStreamWriter writer = new PrettyXMLStreamWriter(sw);
        writer.writeStartElement("Project");
        writer.writeAttribute("language", "java");
        writer.writeStartElement("Developer");
        writer.writeAttribute("id", "101010");
        writer.writeStartElement("name");
        writer.writeCData(Arrays.asList("Hawk<>12", "Hawk13", "Hawk14"));
        writer.writeEndElement();
        writer.writeStartElement("email");
        writer.writeCharacters("fenris@hotmail.com");
        writer.writeEndElement();
        writer.writeEmptyElement("empty1");
        writer.writeAttribute("hello", "x");
        writer.writeAttribute("hello2", "x");
        writer.writeEmptyElement("empty2");
        writer.writeAttribute("hello", "x");
        writer.writeEndElement();

        writer.writeEmptyElement("empty11");
        writer.writeEmptyElement("empty12");
        writer.writeEmptyElement("empty13");
        writer.writeEmptyElement("empty14");

        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
        assertThat(sw.toString()).isEqualTo(FileReadHelper.readAllText("/glz/hawk/codepoet/xml/test2.xml").trim());
    }

}
