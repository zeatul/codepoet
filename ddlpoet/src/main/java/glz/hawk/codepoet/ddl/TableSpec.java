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

import glz.hawkframework.core.helper.StringHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class TableSpec {

    public final String name;
    public final String comment;
    public final PrimaryKeySpec primaryKeySpec;
    public final List<ColumnSpec> columnSpecs;
    public final List<IndexSpec> indexSpecs;
    public final List<String> suffixes ; // 在表定义语句的末尾提供额外的属性


    private TableSpec(Builder builder) {
        this.name = builder.name;
        this.comment = builder.comment;
        this.primaryKeySpec = builder.primaryKeySpec;
        this.columnSpecs = Collections.unmodifiableList(builder.columnSpecs);
        this.indexSpecs = Collections.unmodifiableList(builder.indexSpecs);
        this. suffixes = Collections.unmodifiableList(builder.suffixes);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    void emit(DatabaseCodeWriter codeWriter) throws IOException {
        //create a title
        String tag = "==============================================================";
        codeWriter.emit("/*").emit(tag).emit("*/").emitNewLine();
        String title = String.format(" Table: %s", name);
        codeWriter.emit("/*").emit(title).emit(StringHelper.repeatChar(' ', tag.length() - title.length())).emit("*/").emitNewLine();
        codeWriter.emit("/*").emit(tag).emit("*/").emitNewLine();

        //drop table
        codeWriter.dialectSupport.dropTable(codeWriter, this).emitNewLine();

        codeWriter.emitNewLine();
        //create table
        codeWriter.dialectSupport.createTable(codeWriter, this).emitNewLine();

        //table comment
        if (codeWriter.dialectSupport.isCreateTableCommentIndividually()) {
            if (StringHelper.isNotBlank(comment)) {
                codeWriter.emitNewLine();
                codeWriter.dialectSupport.createTableComment(codeWriter, this).emitNewLine();
            }
        }

        //column comment
        if (codeWriter.dialectSupport.isCreateColumnCommentIndividually()) {
            for (ColumnSpec columnSpec : columnSpecs) {
                if (StringHelper.isNotBlank(columnSpec.comment)) {
                    codeWriter.emitNewLine();
                    codeWriter.dialectSupport.createColumnComment(codeWriter, this, columnSpec).emitNewLine();
                }
            }
        }

        //index
        for (IndexSpec indexSpec : indexSpecs) {
            codeWriter.emitNewLine();
            codeWriter.dialectSupport.createIndex(codeWriter, this, indexSpec).emitNewLine();
        }
    }

    public static final class Builder {
        private final String name;
        private final List<ColumnSpec> columnSpecs = new ArrayList<>();
        private final List<IndexSpec> indexSpecs = new ArrayList<>();
        private String comment;
        private PrimaryKeySpec primaryKeySpec;
        private final List<String> suffixes = new ArrayList<>();

        private Builder(String name) {
            //TODO:校验表的名字是否符合sql规范
            this.name = argNotBlank(name, "name");
        }

        public Builder setComment(String comment) {
            //TODO:校验表的comment是否符合sql规范
            this.comment = argNotBlank(comment, "comment");
            return this;
        }

        public Builder addColumn(ColumnSpec columnSpec) {
            columnSpecs.add(argNotNull(columnSpec, "columnSpec"));
            return this;
        }

        public Builder addColumn(String name, DataTypeSpec dataTypeSpec, boolean nullable) {
            return addColumn(ColumnSpec.builder(name, dataTypeSpec, nullable).build());
        }

        public Builder addColumn(String name, DataTypeSpec dataTypeSpec, boolean nullable, String comment) {
            return addColumn(ColumnSpec.builder(name, dataTypeSpec, nullable, comment).build());
        }

        public Builder addIndex(IndexSpec indexSpec) {
            indexSpecs.add(argNotNull(indexSpec, "indexSpec"));
            return this;
        }

        public Builder setPrimaryKey(PrimaryKeySpec primaryKeySpec) {
            this.primaryKeySpec = argNotNull(primaryKeySpec, "primaryKeySpec");
            return this;
        }

        public Builder setPrimaryKey(String... columnNames) {
            this.primaryKeySpec = PrimaryKeySpec.builder(String.format("PK_%s", name)).addIndexColumns(columnNames).build();
            return this;
        }

        public Builder addSuffix(String suffix) {
            this.suffixes.add(argNotBlank(suffix, "suffix"));
            return this;
        }

        public Builder addSuffixes(String... suffixes) {
            return addSuffixes(Arrays.asList(suffixes));
        }

        public Builder addSuffixes(List<String> suffixes) {
            this.suffixes.addAll(argNoBlankElement(suffixes, "suffixes"));
            return this;
        }

        public TableSpec build() {
            //TODO:校验
            return new TableSpec(this);
        }
    }
}
