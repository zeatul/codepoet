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

package glz.hawk.codepoet.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;
import static glz.hawkframework.core.support.LogicSupport.throwIfFalse;

/**
 * Code blocks support placeholders like {@link java.text.Format}. Where {@link String#format}
 * uses percent {@code %} to reference target values, this class uses dollar sign {@code $} and the
 * permitted placeholder is described concrete class.
 *
 * @author Hawk
 */
@SuppressWarnings("ALL")
public abstract class AbstractCodeBlock {
    private static final Pattern NAMED_ARGUMENT =
            Pattern.compile("\\$(?<argumentName>[\\w_]+):(?<typeChar>\\w).*");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]+[\\w_]*");

    final public List<String> formatParts;
    final public List<Object> args;

    protected AbstractCodeBlock(AbstractCodeBlockBuilder builder) {
        this.formatParts = Collections.unmodifiableList(builder.formatParts);
        this.args = Collections.unmodifiableList(builder.args);
    }

    public boolean isEmpty() {
        return formatParts.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }


    public static abstract class AbstractCodeBlockBuilder<K extends AbstractCodeBlock, T extends AbstractCodeBlockBuilder<K, T>> {
        final protected List<String> formatParts = new ArrayList<>();
        final protected List<Object> args = new ArrayList<>();

        protected Character[] noArgPlaceholders;

        protected Character[] argPlaceholders;

        protected AbstractCodeBlockBuilder(Character[] noArgPlaceholders, Character[] argPlaceholders) {
            this.noArgPlaceholders = noArgPlaceholders;
            this.argPlaceholders = argPlaceholders;
        }

        public boolean isEmpty() {
            return formatParts.isEmpty();
        }

        /**
         * Adds code using named arguments.
         * <p>Named arguments specify their name after the '$' followed by: and the corresponding type character.
         * Argument names consist of characters in a-z, A-Z, 0-9, and _ and must start with a lowercase character.</p>
         * <p>For example, to refer to the type Integer with the argument name clazz use a format string containing $clazz:T
         * and include the key clazz with value java.lang.Integer.class in the argument map.</p>
         */
        public T addNamed(String format, Map<String, ?> arguments) {
            int p = 0;
            while (p < format.length()) {
                int nextP = format.indexOf("$", p);
                if (nextP == -1) {
                    formatParts.add(format.substring(p));
                    break;
                }

                if (p != nextP) {
                    formatParts.add(format.substring(p, nextP));
                    p = nextP;
                }

                Matcher matcher = null;
                int colon = format.indexOf(':', p);
                if (colon != -1) {
                    int endIndex = Math.min(colon + 2, format.length());
                    matcher = NAMED_ARGUMENT.matcher(format.substring(p, endIndex));
                }
                if (matcher != null && matcher.lookingAt()) {
                    String argumentName = matcher.group("argumentName");
                    argument(argumentName, arguments::containsKey, s -> String.format("Missing named argument for $%s", s));
                    char formatChar = matcher.group("typeChar").charAt(0);
                    validArgPlaceHolder(formatChar);
                    addArgument(format, formatChar, arguments.get(argumentName));
                    formatParts.add("$" + formatChar);
                    p += matcher.regionEnd();
                } else {
                    argument(p, index -> index < format.length() - 1, index -> "dangling $ at end");
                    argument(format.charAt(p + 1), this::isNoArgPlaceholder, index -> String.format("unknown format $%s at %s in '%s', the following no arg place holder is legal: %s"
                            , format.charAt(index + 1), index + 1, format, noArgPlaceHolderDesc()));
                    p += 2;
                }
            }
            return (T) this;
        }

        /**
         * Add code with positional or relative arguments.
         * <p>Relative arguments map 1:1 with the placeholders in the format string.</p>
         * <p>Positional arguments use an index after the placeholder to identify which argument index to use.
         * For example, for a literal to reference the 3rd argument: "$3L" (1 based index)</p>
         * <p>Mixing relative and positional arguments in a call to add is invalid and will result in an error.</p>
         */
        public T add(String format, Object... args) {
            argNotNull(format, "format");

            boolean hasRelative = false; //flag for supporting relative position for parameter replacement
            boolean hasIndexed = false; //flag for supporting indexed position for parameter replacement
            int relativeParameterCount = 0; // the count of relative parameter.
            int[] indexedParameterCount = new int[args.length];

            for (int p = 0; p < format.length(); ) {
                if (format.charAt(p) != '$') {
                    int nextP = format.indexOf('$', p + 1); // the position of '$' after p
                    if (nextP == -1) {
                        nextP = format.length();
                    }
                    formatParts.add(format.substring(p, nextP));
                    p = nextP;
                    continue;
                }

                // charAt(p) is '$'
                p++;

                // Consume zero or more digits, leaving 'c' as the first non-digit char after the '$'.
                int indexStart = p; // the first char position after '$'
                char c;
                do {
                    //the last character after '$' must not be digit and must exist.
                    throwIfFalse(p < format.length(), () -> new IllegalArgumentException(String.format("dangling format characters in '%s'", format)));
                    c = format.charAt(p++);
                } while (c >= '0' && c <= '9');
                int indexEnd = p - 1;

                // If 'c' doesn't take an argument, we're done.
                if (isNoArgPlaceholder(c)) {
                    throwIfFalse(indexStart == indexEnd, () -> new IllegalArgumentException(String.format("No arg place holder: %s may not have an index", noArgPlaceHolderDesc())));
                    formatParts.add("$" + c);
                    continue;
                }

                // must be valid arg place holder
                validArgPlaceHolder(c);

                int index;
                if (indexStart < indexEnd) {
                    index = Integer.parseInt(format.substring(indexStart, indexEnd)) - 1;
                    hasIndexed = true;
                    if (args.length > 0) {
                        // one parameter must have at least one corresponding index.
                        indexedParameterCount[index % args.length]++; // computer the parameter count for specific index.
                    }
                } else {
                    index = relativeParameterCount; //relative parameter index is 0,1,2,3......
                    hasRelative = true;
                    relativeParameterCount++;
                }

                // The index must be legal
                throwIfFalse(index >= 0 && index < args.length, () -> new IllegalArgumentException(String.format("index %d for '%s' not in range (received %s arguments)",
                        index + 1, format.substring(indexStart - 1, indexEnd + 1), args.length)));

                // Can't support indexed parameter and relative parameter at the same time.
                throwIfFalse(!hasIndexed || !hasRelative, () -> new IllegalArgumentException("cannot mix indexed and positional parameters"));

                addArgument(format, c, args[index]);
                formatParts.add("$" + c);
            }

            if (hasRelative) {
                final String s = relativeParameterCount + "";
                throwIfFalse(relativeParameterCount >= args.length, () -> new IllegalArgumentException(String.format("unused arguments: expected %s, received %d", s, args.length)));
            }

            if (hasIndexed) {
                List<String> unused = new ArrayList<>();
                for (int i = 0; i < args.length; i++) {
                    if (indexedParameterCount[i] == 0) {
                        unused.add("$" + (i + 1));
                    }
                }
                String s = unused.size() == 1 ? "" : "s";
                throwIfFalse(unused.isEmpty(), () -> new IllegalArgumentException(String.format("unused argument%s: %s", s, String.join(", ", unused))));
            }

            return (T) this;
        }

        private boolean isNoArgPlaceholder(char c) {
            for (char x : noArgPlaceholders) {
                if (x == c) {
                    return true;
                }
            }
            return false;
        }

        private String noArgPlaceHolderDesc() {
            return Stream.of((noArgPlaceholders)).map(c -> "$" + c).collect(Collectors.joining(", "));
        }

        private void validArgPlaceHolder(char c) {
            if (!isArgPlaceholder(c)) {
                throw new IllegalArgumentException(String.format("$%s is not a legal argument place holder, the following place holder is legal: %s", c,
                        Stream.of(argPlaceholders).map(cc -> "$" + cc).collect(Collectors.joining(", "))));
            }
        }

        private String argPlaceHolderDesc() {
            return Stream.of(argPlaceholders).map(c -> "$" + c).collect(Collectors.joining(", "));
        }

        private boolean isArgPlaceholder(char c) {
            for (char x : argPlaceholders) {
                if (x == c) {
                    return true;
                }
            }
            return false;
        }

        protected abstract void addArgument(String format, char c, Object arg);


        public T add(K codeBlock) {
            argNotNull(codeBlock, "codeBlock");
            formatParts.addAll(codeBlock.formatParts);
            args.addAll(codeBlock.args);
            return (T) this;
        }

    }
}
