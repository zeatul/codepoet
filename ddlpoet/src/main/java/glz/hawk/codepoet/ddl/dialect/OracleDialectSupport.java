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

import glz.hawk.codepoet.ddl.DatabaseCodeWriter;
import glz.hawk.codepoet.ddl.ColumnSpec;
import glz.hawk.codepoet.ddl.DataTypeSpec;
import glz.hawk.codepoet.ddl.TableSpec;

import java.io.IOException;
import java.sql.Types;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class OracleDialectSupport extends AbstractDialectSupport {
    @Override
    public DatabaseCodeWriter dropTable(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException {
        codeWriter.emit("BEGIN EXECUTE IMMEDIATE 'DROP TABLE ' || '$N' || ' CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;", tableSpec);
        return codeWriter;
    }

    @Override
    public DatabaseCodeWriter createTable(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException {
        return super.createTable(codeWriter, tableSpec).emit(";");
    }

    @Override
    public DatabaseCodeWriter createTableComment(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException {
        return codeWriter.emit("COMMENT ON TABLE $L IS $C;", tableSpec.name, tableSpec.comment);
    }

    @Override
    public DatabaseCodeWriter createColumnComment(DatabaseCodeWriter codeWriter, TableSpec tableSpec, ColumnSpec columnSpec) throws IOException {
        return codeWriter.emit("COMMENT ON COLUMN $L.$L IS $C;", tableSpec.name, columnSpec.name, columnSpec.comment);
    }

    @Override
    public String getTypeName(DataTypeSpec dataTypeSpec) {
        switch (dataTypeSpec.type) {
            case Types.TIME:
            case Types.TIMESTAMP:
                return String.format("TIMESTAMP(%d)", dataTypeSpec.precision);
            default:
                return super.getTypeName(dataTypeSpec);
        }
    }
}
