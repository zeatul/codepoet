/**
 * File Comment
 */

package com.example.test;


public @interface HelloAnnotation {
    String name() default "Venus";

    /**
     * 类型定义"
     */
    String type() default "Star";

    String[] nameList() default {};
}
