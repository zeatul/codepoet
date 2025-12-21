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

import glz.hawk.codepoet.ddl.*;

import java.io.IOException;
import java.sql.Types;
import java.util.List;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class AbstractDialectSupport implements DialectSupport {

    @Override
    public DatabaseCodeWriter createTable(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException {
        codeWriter.emit("CREATE TABLE $L(", tableSpec.name).emitNewLine();
        codeWriter.indent();
        final List<ColumnSpec> columnSpecs = tableSpec.columnSpecs;
        final PrimaryKeySpec primaryKeySpec = tableSpec.primaryKeySpec;
        for (int i = 0; i < columnSpecs.size(); i++) {
            createColumn(codeWriter, columnSpecs.get(i))
                    .emit(i < columnSpecs.size() - 1 || primaryKeySpec != null, ",")
                    .emitNewLine();
        }
        if (primaryKeySpec != null) {
            createPrimaryKey(codeWriter, primaryKeySpec).emitNewLine();
        }
        codeWriter.unindent();
        codeWriter.emit(");");
        return codeWriter;
    }

    @Override
    public DatabaseCodeWriter createColumn(DatabaseCodeWriter codeWriter, ColumnSpec columnSpec) throws IOException {
        return codeWriter.emit(columnSpec.nullable ? "$L $D" : "$L $D NOT NULL", columnSpec.name, columnSpec.dataTypeSpec);
    }

    @Override
    public DatabaseCodeWriter createPrimaryKey(DatabaseCodeWriter codeWriter, PrimaryKeySpec primaryKeySpec) throws IOException {
        codeWriter.emit("CONSTRAINT $L PRIMARY KEY (", primaryKeySpec.name);
        final List<IndexColumnSpec> indexColumnSpecs = primaryKeySpec.indexColumnSpecs;
        for (int i = 0; i < indexColumnSpecs.size(); i++) {
            codeWriter.emit(indexColumnSpecs.get(i).name)
                    .emit(i < indexColumnSpecs.size() - 1, ", ");
        }
        codeWriter.emit(")");
        return codeWriter;
    }

    @Override
    public DatabaseCodeWriter createIndex(DatabaseCodeWriter codeWriter, TableSpec tableSpec, IndexSpec indexSpec) throws IOException {
        String format = indexSpec.isUnique ? "CREATE UNIQUE INDEX $N ON $N(" : "CREATE INDEX $N ON $N(";
        codeWriter.emit(format, indexSpec, tableSpec);
        int j = 0;
        for (IndexColumnSpec indexColumnSpec : indexSpec.indexColumnSpecs) {
            switch (indexColumnSpec.sortType) {
                case ASC:
                    codeWriter.emit("$N ASC", indexColumnSpec);
                    break;
                case DESC:
                    codeWriter.emit("$N DESC", indexColumnSpec);
                    break;
                case DEFAULT:
                    codeWriter.emit("$N", indexColumnSpec);
                    break;
                default:
                    throw new IllegalStateException(String.format("Unsupported SortType: %s", indexColumnSpec.sortType.name()));
            }
            codeWriter.emit(++j != indexSpec.indexColumnSpecs.size(), ", ");
        }
        codeWriter.emit(");");
        return codeWriter;
    }

    @Override
    public String getTypeName(DataTypeSpec dataTypeSpec) {
        final int type = dataTypeSpec.type;
        final int length = dataTypeSpec.length;
        final int precision = dataTypeSpec.precision;
        final int scale = dataTypeSpec.scale;

        switch (dataTypeSpec.type) {
            case Types.CHAR:
                return String.format("CHAR(%d)", length);
            case Types.VARCHAR:
                return String.format("VARCHAR(%d)", length);
            case Types.CLOB:
                return "CLOB";
            case Types.BLOB:
                return "BLOB";
            case Types.DATE:
                return "DATE";
            case Types.TIME:
                return String.format("TIME(%d)", precision);
            case Types.TIMESTAMP:
                return String.format("DATETIME(%d)", precision);
            case Types.NUMERIC:
                return String.format("NUMERIC(%d, %d)", precision, scale);
            default:
                throw new IllegalStateException(String.format("Type(%d) is unsupported now.", type));
        }
    }

    @Override
    public boolean isCreateTableCommentIndividually() {
        return true;
    }

    @Override
    public boolean isCreateColumnCommentIndividually() {
        return true;
    }
}
