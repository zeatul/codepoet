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

package glz.hawk.codepoet.java.javadoc;

import glz.hawk.codepoet.java.JavaCodeBlock;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for java doc flow.
 *
 * @author Hawk
 */
public class ComplexJavadocBlockBuilder<T> extends AbstractDocFlow<ComplexJavadocBlockBuilder<T>> {
    private final T parent;
    public ComplexJavadocBlockBuilder(JavaCodeBlock.Builder codeBlockBuilder, T parent) {
        super(codeBlockBuilder);
        this.parent = argNotNull(parent,"parent");
    }

    public T end(){
        return this.parent;
    }
}
