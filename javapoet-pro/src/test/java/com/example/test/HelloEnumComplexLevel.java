package com.example.test;

import glz.hawkframework.core.support.ArgumentSupport;

enum HelloEnumComplexLevel {
    A("namedAAA"),
    B("namedBBB");

    final private String name;

    HelloEnumComplexLevel(String name) {
        this.name = ArgumentSupport.argNotBlank(name,"name");
    }

    public String getName() {
        return this.name;
    }
}
