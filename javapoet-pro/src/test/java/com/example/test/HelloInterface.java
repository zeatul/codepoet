package com.example.test;

import java.io.Serializable;
import java.util.List;

public interface HelloInterface extends List, Serializable {
    default void test() {
    }
}
