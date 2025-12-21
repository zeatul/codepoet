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

import javax.annotation.Nullable;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class ColumnSpec {

    public final String name;
    public final DataTypeSpec dataTypeSpec;
    public final boolean nullable;
    public final String comment;


    private ColumnSpec(Builder builder) {
        this.name = builder.name;
        this.dataTypeSpec = builder.dataTypeSpec;
        this.nullable = builder.nullable;
        this.comment = builder.comment;
    }

    public static Builder builder(String name, DataTypeSpec dataTypeSpec, boolean nullable) {
        return new Builder(name, dataTypeSpec, nullable);
    }

    public static Builder builder(String name, DataTypeSpec dataTypeSpec, boolean nullable, String comment) {
        return new Builder(name, dataTypeSpec, nullable).setComment(comment);
    }

    public static final class Builder {
        private final String name;
        private final DataTypeSpec dataTypeSpec;
        private final boolean nullable;
        private String comment;

        private Builder(String name, DataTypeSpec dataTypeSpec, boolean nullable) {
            //TODO: 校验name是否符合sql规范
            this.name = argNotBlank(name, "name");
            this.dataTypeSpec = argNotNull(dataTypeSpec, "dataTypeSpec");
            this.nullable = nullable;
        }

        public Builder setComment(@Nullable String comment) {
            if (comment != null) {
                //TODO: 校验comment的合法性：长度，正则
            }
            this.comment = comment;
            return this;
        }

        public ColumnSpec build() {
            //TODO:校验
            return new ColumnSpec(this);
        }
    }
}
