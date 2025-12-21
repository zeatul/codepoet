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

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static glz.hawk.codepoet.ddl.DataTypeSpec.*;
import static glz.hawk.codepoet.ddl.dialect.Dialect.MYSQL;
import static glz.hawk.codepoet.ddl.dialect.Dialect.ORACLE;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class SqlFileTest {

    private final DatabaseSpec databaseSpec;

    public SqlFileTest() {
        TableSpec bookTable = TableSpec.builder("BOOK")
                .setComment("书''书")
                .addColumn("BOOK_ID", ofNumeric(38), false)
                .addColumn("ISBN", ofVarchar(120), false)
                .addColumn(ColumnSpec.builder("BOOK_NAME", ofVarchar(100), false, "书名").build())
                .addColumn("BOOK_TYPE", ofVarchar(20), false, "书类型")
                .addColumn("BOOK_PRICE", ofNumeric(19, 2), false, "书'单价")
                .addColumn("PUBLISH_DATE", DATE, true, "发布日期")
                .addColumn("BOOK_DESCRIPTION", CLOB, true)
                .addColumn("BOOK_PICTURE", BLOB, true)
                .addColumn("TIME_SPEC", ofTime(0), true)
                .addColumn("CREATE_DATE", ofTimestamp(3), true, "创建时间")
                .addColumn("CREATE_USER", ofChar(8), true, "创建者工号")
                .setPrimaryKey("BOOK_ID")
                .addIndex(IndexSpec.builder("UI_BOOK_ISBN", true)
                        .addIndexColumns("ISBN")
                        .build())
                .addIndex(IndexSpec.builder("I_BOOK_NAME_AND_TYPE", false)
                        .addIndexColumns("BOOK_TYPE", "BOOK_NAME")
                        .build())
                .addIndex(IndexSpec.builder("I_BOOK_NAME_PRICE_AND_DATE", false)
                        .addIndexColumn(IndexColumnSpec.builder("BOOK_NAME", SortType.DEFAULT).build())
                        .addIndexColumn("BOOK_PRICE", SortType.ASC).addIndexColumn("PUBLISH_DATE", SortType.DESC)
                        .build())
                .build();

        TableSpec authorTable = TableSpec.builder("AUTHOR")
                .setComment("作者")
                .addColumn("AUTHOR_ID", ofNumeric(38), false)
                .addColumn("AUTHOR_NAME", ofVarchar(100), false)
                .setPrimaryKey("AUTHOR_ID")
                .addIndex(IndexSpec.builder("I_AUTHOR_NAME", false)
                        .addIndexColumns("AUTHOR_NAME")
                        .build())
                .build();

        TableSpec bookAuthorMapTable = TableSpec.builder("BOOK_AUTHOR_MAP")
                .setComment("书和作者的对应关系")
                .addColumn("BOOK_AUTHOR_MAP_ID", ofNumeric(38), false)
                .addColumn("BOOK_ID", ofNumeric(38), false)
                .addColumn("AUTHOR_ID", ofNumeric(38), false)
                .setPrimaryKey("BOOK_AUTHOR_MAP_ID").addIndex(IndexSpec.builder("UI_BOOK_AUTHOR_MAP", true)
                        .addIndexColumns("BOOK_ID", "AUTHOR_ID")
                        .build())
                .build();

        this.databaseSpec = DatabaseSpec.builder()
                .addTableSpec(bookTable)
                .addTableSpec(authorTable)
                .addTableSpec(bookAuthorMapTable)
                .build();
    }

    @Test
    public void testOracle() throws IOException {
        System.out.println("-----------Oracle Demonstration-------------------");
        SqlFile sqlFile = SqlFile.builder(databaseSpec, ORACLE).build();
        sqlFile.writeTo(System.out);
        System.out.println();
//        File file = getFile("e:/temp/book_store_oracle.sql");
//        sqlFile.writeTo(file);
    }

    @Test
    public void testMysql() throws IOException {
        System.out.println("-----------Mysql Demonstration-------------------");
        SqlFile sqlFile = SqlFile.builder(databaseSpec, MYSQL).build();
        sqlFile.writeTo(System.out);
        System.out.println();
//        File file = getFile("e:/temp/book_store_mysql.sql");
//        sqlFile.writeTo(file);
    }

    private File getFile(String absolutePath) throws IOException {
        File file = new File(absolutePath);
        if (file.exists()) {
            Files.deleteIfExists(file.toPath());
        }
        return file;
    }
}
