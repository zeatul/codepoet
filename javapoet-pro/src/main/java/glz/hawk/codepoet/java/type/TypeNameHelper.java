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

package glz.hawk.codepoet.java.type;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import javax.lang.model.util.SimpleTypeVisitor8;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static glz.hawk.codepoet.java.type.PrimitiveTypeName.*;
import static glz.hawk.codepoet.java.type.VoidTypeName.VOID;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class TypeNameHelper {

    public static TypeName ofType(Type type) {
        argNotNull(type, "type");
        if (type instanceof Class) {
            if (type == void.class) {
                return VOID;
            }
            if (type == boolean.class) {
                return BOOLEAN;
            }
            if (type == byte.class) {
                return BYTE;
            }
            if (type == short.class) {
                return SHORT;
            }
            if (type == int.class) {
                return INT;
            }
            if (type == long.class) {
                return LONG;
            }
            if (type == float.class) {
                return FLOAT;
            }
            if (type == double.class) {
                return DOUBLE;
            }

            Class<?> clazz = (Class<?>) type;

            if (clazz.isArray()) {
                return ArrayTypeName.ofType(clazz.getComponentType());
            }

            return ClassName.ofClass(clazz);
        }

        throw new IllegalArgumentException(String.format("unexpected type: %s", type));
    }

    public static TypeName ofTypeMirror(TypeMirror typeMirror) {
        return typeMirror.accept(new SimpleTypeVisitor8<TypeName, Void>() {
            @Override
            public TypeName visitPrimitive(PrimitiveType t, Void unused) {
                switch (t.getKind()) {
                    case BOOLEAN:
                        return PrimitiveTypeName.BOOLEAN;
                    case BYTE:
                        return PrimitiveTypeName.BYTE;
                    case SHORT:
                        return PrimitiveTypeName.SHORT;
                    case INT:
                        return PrimitiveTypeName.INT;
                    case LONG:
                        return PrimitiveTypeName.LONG;
                    case CHAR:
                        return PrimitiveTypeName.CHAR;
                    case FLOAT:
                        return PrimitiveTypeName.FLOAT;
                    case DOUBLE:
                        return PrimitiveTypeName.DOUBLE;
                    default:
                        throw new IllegalStateException("Unexpected PrimitiveType: " + t);
                }
            }

            @Override
            public TypeName visitDeclared(DeclaredType t, Void unused) {
                ClassName rawType = ClassName.of((TypeElement) t.asElement());

                List<TypeName> typeArgumentNames = new ArrayList<>();
                t.getTypeArguments().stream().map(TypeNameHelper::ofTypeMirror).forEach(typeArgumentNames::add);

                return typeArgumentNames.isEmpty() ? rawType : ParameterizedTypeName.of(rawType, typeArgumentNames);
            }

            @Override
            public TypeName visitError(ErrorType t, Void unused) {
                return visitDeclared(t, unused);
            }

            @Override
            public TypeName visitArray(ArrayType t, Void unused) {
                return ArrayTypeName.ofTypeName(ofTypeMirror(t.getComponentType()));
            }

            @Override
            public TypeName visitTypeVariable(TypeVariable t, Void unused) {
                return TypeVariableName.of(t);
            }

            @Override
            public TypeName visitWildcard(WildcardType t, Void unused) {
                return WildcardTypeName.of(t);
            }

            @Override
            public TypeName visitNoType(NoType t, Void unused) {
                if (t.getKind() == TypeKind.VOID) {
                    return VOID;
                } else {
                    return super.visitNoType(t, unused);
                }
            }

            @Override
            protected TypeName defaultAction(TypeMirror e, Void unused) {
                throw new IllegalStateException("Unsupported TypeMirror: " + e);
            }
        }, null);
    }

    public static boolean isInnerClass(TypeElement typeElement) {
        Element enclosingElement = typeElement.getEnclosingElement();

        if (enclosingElement == null) {
            return false; // 顶级类
        }

        ElementKind enclosingKind = enclosingElement.getKind();

        // 内部类的封闭元素应该是类、接口、枚举或注解
        return enclosingKind == ElementKind.CLASS ||
            enclosingKind == ElementKind.INTERFACE ||
            enclosingKind == ElementKind.ENUM ||
            enclosingKind == ElementKind.ANNOTATION_TYPE;
    }
}
