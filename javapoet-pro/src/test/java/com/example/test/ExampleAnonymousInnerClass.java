package com.example.test;

import java.util.function.Consumer;

public class ExampleAnonymousInnerClass {
    Consumer<Integer> consumer = new Consumer<Integer>() {
        public void accept(Integer integer) {
        }
    };

    void m1(Consumer<Integer> consumer) {
    }

    void m2() {
        m1(new Consumer<Integer>() {
            public void accept(Integer integer) {
            }
        });
    }
}
