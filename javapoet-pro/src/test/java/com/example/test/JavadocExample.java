/**
 * File Comment One.
 * File Comment Two.
 */

package com.example.test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * First, Second, Three
 * Type comment.
 * The addDocument method adds a new line in the end, but addDoc method doesn't add it.
 * <p>
 * {@link Map map}'s value is {@code true}
 * {@link List list}'s value is {@code true}
 * </p>
 *  
 * @param <K> type parameter 1
 * @param <V> type parameter 2
 * @author Hawk
 */
public class JavadocExample<K, V> {
    /**
     * My name is Wanted
     *  
     * @since JDK1.1
     */
    public String name;

    /**
     * The method javadoc example.
     *  
     * @param a parameter a
     * @param b parameter b
     * @return the sum value
     * @throws IOException io error was found.
     * @deprecated 
     */
    long sum(long a, long b) throws Exception {
        return a + b;
    }
}
