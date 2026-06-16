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

package glz.hawk.codepoet.ddl.dialect;

import glz.hawk.codepoet.ddl.ColumnSpec;
import glz.hawk.codepoet.ddl.DataTypeSpec;
import glz.hawk.codepoet.ddl.DatabaseCodeWriter;
import glz.hawk.codepoet.ddl.TableSpec;
import glz.hawkframework.core.helper.StringHelper;

import java.io.IOException;
import java.sql.Types;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class MysqlDialectSupport extends AbstractDialectSupport {
    @Override
    public DatabaseCodeWriter dropTable(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException {
        codeWriter.emit("DROP TABLE IF EXISTS $N;", tableSpec);
        return codeWriter;
    }

    @Override
    public DatabaseCodeWriter createTable(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException {
        super.createTable(codeWriter, tableSpec);
        final String comment = tableSpec.comment;
        if (StringHelper.isNotBlank(comment)) {
            codeWriter.emit(" COMMENT $C", comment);
        }
        // customizer
        codeWriter.indent();
        for (String suffix : tableSpec.suffixes){
            codeWriter.emitNewLine();
            codeWriter.emit(suffix);
        }
        codeWriter.emit(";");
        codeWriter.unindent();
        return codeWriter;
    }

    @Override
    public DatabaseCodeWriter createTableComment(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException {
        throw new UnsupportedOperationException("Mysql doesn't need create table comment individually");
    }

    @Override
    public String getTypeName(DataTypeSpec dataTypeSpec) {
        if (dataTypeSpec.type == Types.CLOB) {
            return "TEXT";
        } else {
            return super.getTypeName(dataTypeSpec);
        }
    }

    @Override
    public DatabaseCodeWriter createColumn(DatabaseCodeWriter codeWriter, ColumnSpec columnSpec) throws IOException {
        super.createColumn(codeWriter, columnSpec);
        final String comment = columnSpec.comment;
        return codeWriter.emit(StringHelper.isNotBlank(comment), " COMMENT $C", comment);
    }

    @Override
    public DatabaseCodeWriter createColumnComment(DatabaseCodeWriter codeWriter, TableSpec tableSpec, ColumnSpec columnSpec) throws IOException {
        throw new UnsupportedOperationException("Mysql doesn't need create column comment individually");
    }

    @Override
    public boolean isCreateTableCommentIndividually() {
        return false;
    }

    @Override
    public boolean isCreateColumnCommentIndividually() {
        return false;
    }
}
