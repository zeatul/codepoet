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

/**
 * This interface is responsible for
 *
 * @author Hawk
 */
public interface DialectSupport {

    DatabaseCodeWriter dropTable(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException;

    DatabaseCodeWriter createTable(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException;

    DatabaseCodeWriter createTableComment(DatabaseCodeWriter codeWriter, TableSpec tableSpec) throws IOException;

    DatabaseCodeWriter createColumn(DatabaseCodeWriter codeWriter, ColumnSpec columnSpec) throws IOException;

    DatabaseCodeWriter createColumnComment(DatabaseCodeWriter codeWriter, TableSpec tableSpec, ColumnSpec columnSpec) throws IOException;

    DatabaseCodeWriter createPrimaryKey(DatabaseCodeWriter codeWriter, PrimaryKeySpec primaryKeySpec) throws IOException;

    DatabaseCodeWriter createIndex(DatabaseCodeWriter codeWriter, TableSpec tableSpec, IndexSpec indexSpec) throws IOException;

    String getTypeName(DataTypeSpec dataTypeSpec);

    boolean isCreateTableCommentIndividually();

    boolean isCreateColumnCommentIndividually();
}
