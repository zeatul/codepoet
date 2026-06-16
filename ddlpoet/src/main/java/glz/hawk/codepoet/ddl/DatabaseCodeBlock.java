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

import glz.hawk.codepoet.core.AbstractCodeBlock;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DatabaseCodeBlock extends AbstractCodeBlock {

    private DatabaseCodeBlock(Builder builder) {
        super(builder);
    }

    public static DatabaseCodeBlock of(String format, Object... args) {
        return new Builder().add(format, args).build();
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder  extends AbstractCodeBlockBuilder<DatabaseCodeBlock,Builder>{
        final static Character[] NO_ARG_PLACE_HOLDERS = new Character[]{'$', '>', '<', '[', ']', 'W', 'R'};
        final static Character[] ARG_PLACE_HOLDERS = new Character[]{'N', 'L','D', 'C'};

        private Builder() {
            super(NO_ARG_PLACE_HOLDERS, ARG_PLACE_HOLDERS);
        }

        @Override
        protected void addArgument(String format, char c, Object arg) {
            switch (c) {
                case 'N':
                    this.args.add(argToName(arg));
                    break;
                case 'L':
                    this.args.add(argToLiteral(arg));
                    break;
                case 'D':
                    if (arg instanceof DataTypeSpec) {
                        this.args.add(argToLiteral(arg));
                    } else {
                        throw new IllegalArgumentException("The corresponding arg must be an instance of DataTypeSpec for $D");
                    }
                    break;
                case 'C':
                    this.args.add(argToString(arg));
                    break;
                default:
                    throw new IllegalArgumentException(
                            String.format("invalid format string: '%s'", format));
            }
        }

        private String argToName(Object o) {
            if (o instanceof CharSequence) return o.toString();
            if (o instanceof TableSpec) return ((TableSpec) o).name;
            if (o instanceof ColumnSpec) return ((ColumnSpec) o).name;
            if (o instanceof IndexSpec) return ((IndexSpec) o).name;
            if (o instanceof PrimaryKeySpec) return ((PrimaryKeySpec) o).name;
            if (o instanceof IndexColumnSpec) return ((IndexColumnSpec) o).name;
            throw new IllegalArgumentException("expected name but was " + o);
        }

        private Object argToLiteral(Object o) {
            return o;
        }

        private String argToString(Object o) {
            return o != null ? String.valueOf(o) : null;
        }


        public DatabaseCodeBlock build() {
            return new DatabaseCodeBlock(this);
        }
    }
}
