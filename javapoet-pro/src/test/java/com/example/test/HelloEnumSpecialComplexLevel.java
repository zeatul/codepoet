package com.example.test;

import glz.hawkframework.core.support.ArgumentSupport;

import java.util.UUID;

public enum HelloEnumSpecialComplexLevel {
    /**
     * Hello World, namedAAA
     */
    A("namedAAA") {
        @Override
        public String getRandomName() {
            return this.name() + System.currentTimeMillis();
        }
    },
    /**
     * namedBBB
     */
    B("namedBBB") {
        @Override
        public String getRandomName() {
            return this.name() + UUID.randomUUID();
        }
    };

    final private String name;

    HelloEnumSpecialComplexLevel(String name) {
        this.name = ArgumentSupport.argNotBlank(name,"name");
    }

    public String getName() {
        return this.name;
    }

    public abstract String getRandomName();
}
