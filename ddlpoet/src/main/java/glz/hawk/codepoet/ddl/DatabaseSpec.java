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

package glz.hawk.codepoet.ddl;

import javax.lang.model.element.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static glz.hawkframework.core.support.ArgumentSupport.argElementNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DatabaseSpec {

    public final List<TableSpec> tableSpecs;
    public final List<Element> originatingElements;

    private DatabaseSpec(Builder builder) {
        this.tableSpecs = Collections.unmodifiableList(builder.tableSpecs);
        this.originatingElements = builder.originatingElements;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder(){
        Builder builder = new Builder();
        builder.addElements(originatingElements);
        builder.addTableSpecs(tableSpecs);
        return builder;
    }

    void emit(DatabaseCodeWriter codeWriter) throws IOException {
        int j = -1;
        for (TableSpec tableSpec : tableSpecs) {
            tableSpec.emit(codeWriter);
            j++;
            if (j < tableSpecs.size() - 1) {
                codeWriter.emitNewLine();
            }
        }
    }

    public static final class Builder {
        private final List<TableSpec> tableSpecs = new ArrayList<>();
        private final List<Element> originatingElements = new ArrayList<>();

        private Builder() {
        }

        public Builder addTableSpec(TableSpec tableSpec) {
            tableSpecs.add(argNotNull(tableSpec, "tableSpec"));
            return this;
        }

        public Builder addTableSpecs(List<TableSpec> tableSpecs) {
            tableSpecs.addAll(argNotNull(tableSpecs, "tableSpecs"));
            return this;
        }

        public Builder addElement(Element element) {
            this.originatingElements.add(argNotNull(element, "element"));
            return this;
        }

        public Builder addElements(Element... elements) {
            addElements(Arrays.asList(elements));
            return this;
        }

        public Builder addElements(Iterable<Element> elements) {
            int index = -1;
            for (Element element : argNotNull(elements, "elements")) {
                this.originatingElements.add(argElementNotNull(element, ++index, "elements"));
            }
            return this;
        }

        public DatabaseSpec build() {
            //TODO:校验
            return new DatabaseSpec(this);
        }
    }
}
