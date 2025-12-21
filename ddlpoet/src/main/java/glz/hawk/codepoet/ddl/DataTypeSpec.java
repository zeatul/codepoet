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

import java.sql.Types;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DataTypeSpec {
    public final static DataTypeSpec CLOB = clobBuilder().build();
    public final static DataTypeSpec BLOB = blobBuilder().build();
    public final static DataTypeSpec DATE = dateBuilder().build();

    public final int type;
    public final int length;
    public final int precision;
    public final int scale;

    private DataTypeSpec(Builder builder) {
        this.type = builder.type;
        this.length = builder.length;
        this.precision = builder.precision;
        this.scale = builder.scale;
    }

    public static DataTypeSpec ofChar(int length) {
        return charBuilder(length).build();
    }

    public static DataTypeSpec ofVarchar(int length) {
        return varcharBuilder(length).build();
    }

    public static DataTypeSpec ofNumeric(int precision, int scale) {
        return numericBuilder(precision, scale).build();
    }

    public static DataTypeSpec ofNumeric(int precision) {
        return numericBuilder(precision, 0).build();
    }

    public static DataTypeSpec ofTime(int precision) {
        return timeBuilder(precision).build();
    }

    public static DataTypeSpec ofTimestamp(int precision) {
        return timestampBuilder(precision).build();
    }

     private static Builder clobBuilder() {
        return new Builder(Types.CLOB, 0, 0, 0);
    }

    private static Builder blobBuilder() {
        return new Builder(Types.BLOB, 0, 0, 0);
    }

    private static Builder charBuilder(int length) {
        return new Builder(Types.CHAR, length, 0, 0);
    }

    private static Builder varcharBuilder(int length) {
        return new Builder(Types.VARCHAR, length, 0, 0);
    }

    private static Builder numericBuilder(int precision, int scale) {
        return new Builder(Types.NUMERIC, 0, precision, scale);
    }

    private static Builder dateBuilder() {
        return new Builder(Types.DATE, 0, 0, 0);
    }

    private static Builder timeBuilder(int precision) {
        return new Builder(Types.TIME, 0, precision, 0);
    }

    private static Builder timestampBuilder(int precision) {
        return new Builder(Types.TIMESTAMP, 0, precision, 0);
    }

    public static final class Builder {
        private final int type;
        private final int length;
        private final int precision;
        private final int scale;

        private Builder(int type, int length, int precision, int scale) {
            this.type = type;
            this.length = length;
            this.precision = precision;
            this.scale = scale;
        }

        public DataTypeSpec build() {
            //TODO:校验
            return new DataTypeSpec(this);
        }
    }
}
