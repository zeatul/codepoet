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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;

/**
 * This enum is responsible for
 *
 * @author Hawk
 */
public enum Dialect implements DialectSupport {
    ORACLE(new OracleDialectSupport()),
    MYSQL(new MysqlDialectSupport());

    private final static Map<String, Dialect> map = new HashMap<>();

    static {

        map.put(ORACLE.name().toLowerCase(), ORACLE);
        map.put(MYSQL.name().toLowerCase(), MYSQL);
    }

    private final DialectSupport dialectSupport;

    Dialect(DialectSupport dialectSupport) {
        this.dialectSupport = dialectSupport;
    }

    public static Dialect parse(String dialectName) {
        return Optional.of(map.get(argNotBlank(dialectName, "dialectName")))
            .orElseThrow(() -> new IllegalStateException("Found no Dialect matched the dialectName: " + dialectName));

    }

    @Override
    public DatabaseCodeWriter dropTable(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException {
        return dialectSupport.dropTable(codeWriter, tableSpec);
    }

    @Override
    public DatabaseCodeWriter createTable(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException {
        return dialectSupport.createTable(codeWriter, tableSpec);
    }

    @Override
    public DatabaseCodeWriter createTableComment(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException {
        return dialectSupport.createTableComment(codeWriter, tableSpec);
    }

    @Override
    public DatabaseCodeWriter createColumn(DatabaseCodeWriter codeWriter, ColumnSpec columnSpec) throws IOException {
        return dialectSupport.createColumn(codeWriter, columnSpec);
    }

    @Override
    public DatabaseCodeWriter createColumnComment(DatabaseCodeWriter codeWriter, TableSpec tableSpec, ColumnSpec columnSpec) throws IOException {
        return dialectSupport.createColumnComment(codeWriter, tableSpec, columnSpec);
    }

    @Override
    public DatabaseCodeWriter createPrimaryKey(DatabaseCodeWriter codeWriter, PrimaryKeySpec primaryKeySpec) throws IOException {
        return dialectSupport.createPrimaryKey(codeWriter, primaryKeySpec);
    }

    @Override
    public DatabaseCodeWriter createIndex(DatabaseCodeWriter codeWriter, TableSpec tableSpec, IndexSpec indexSpec) throws IOException {
        return dialectSupport.createIndex(codeWriter, tableSpec, indexSpec);
    }

    @Override
    public String getTypeName(DataTypeSpec dataTypeSpec) {
        return dialectSupport.getTypeName(dataTypeSpec);
    }

    @Override
    public boolean isCreateTableCommentIndividually() {
        return dialectSupport.isCreateTableCommentIndividually();
    }

    @Override
    public boolean isCreateColumnCommentIndividually() {
        return dialectSupport.isCreateColumnCommentIndividually();
    }
}
