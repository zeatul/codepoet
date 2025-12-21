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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class IndexSpec {
    public final String name;
    public final boolean isUnique;
    public final List<IndexColumnSpec> indexColumnSpecs;

    private IndexSpec(Builder builder) {
        this.name = builder.name;
        this.isUnique = builder.isUnique;
        this.indexColumnSpecs = Collections.unmodifiableList(builder.indexColumnSpecs);
    }

    public static Builder builder(String name, boolean isUnique) {
        return new Builder(name, isUnique);
    }

    public static final class Builder {
        private final String name;
        private final boolean isUnique;
        private final List<IndexColumnSpec> indexColumnSpecs = new ArrayList<>();

        private Builder(String name, boolean isUnique) {
            //TODO:校验index name是否符合sql规范
            this.name = argNotBlank(name, "name");
            this.isUnique = isUnique;
        }

        public Builder addIndexColumn(IndexColumnSpec indexColumnSpec) {
            indexColumnSpecs.add(argNotNull(indexColumnSpec, "indexColumnSpec"));
            return this;
        }

        public Builder addIndexColumn(String columnName, SortType sortType) {
            indexColumnSpecs.add(IndexColumnSpec.builder(columnName,sortType).build());
            return this;
        }

        public Builder addIndexColumn(String columnName) {
            indexColumnSpecs.add(IndexColumnSpec.builder(columnName,SortType.DEFAULT).build());
            return this;
        }

        public Builder addIndexColumns(String... columnNames){
            argNotEmpty(columnNames,"columnNames");
            for (int i=0; i<columnNames.length; i++){
                addIndexColumn(argNotBlank(columnNames[i],String.format("columnNames[%d]",i)));
            }
            return this;
        }


        public IndexSpec build(){
            //TODO:校验
            return new IndexSpec(this);
        }
    }
}
