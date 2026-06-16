/**
 * File Comment
 */

package com.example.test;

import glz.hawk.codepoet.java.support.annotations.JsonFormat;
import glz.hawk.codepoet.java.support.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

public class ExampleAnnotatedTypeName {

    List<String> f1;

    List<@JsonFormat(pattern = "yyyy-MM-dd") @NotNull LocalDate> f2;

    public @JsonFormat(pattern = "yyyy-MM-dd") @NotNull LocalDate today() {
        return LocalDate.now();
    }
}
