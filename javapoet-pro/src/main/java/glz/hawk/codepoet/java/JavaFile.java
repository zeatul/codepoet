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

package glz.hawk.codepoet.java;

import glz.hawk.codepoet.java.javadoc.FileJavadoc;
import glz.hawk.codepoet.java.type.AlwaysQualifiedNamesHolder;
import glz.hawkframework.core.helper.ObjectHelper;
import glz.hawkframework.core.helper.StringHelper;
import glz.hawkframework.core.support.LogicOptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class JavaFile {
    public final FileJavadoc javadoc;
    public final String packageName;
    public final TypeSpec typeSpec;
    private final Logger LOG = LoggerFactory.getLogger(JavaFile.class);
    private final String indent;
    private final String lineSeparator;
    private final List<Element> originatingElements;

    private JavaFile(Builder builder) {
        this.javadoc = builder.javadoc;
        this.packageName = builder.packageName;
        this.typeSpec = builder.typeSpec;
        this.indent = builder.indent;
        this.lineSeparator = builder.lineSeparator;
        this.originatingElements = builder.originatingElements;
    }

    public static Builder builder(String packageName, TypeSpec typeSpec) {
        return new Builder(packageName, typeSpec);
    }

    public Builder toBuilder() {
        Builder builder = new Builder(packageName, typeSpec);
        builder.setJavadoc(javadoc);
        builder.setIndent(indent);
        builder.setLineSeparator(lineSeparator);
        builder.addOriginatingElements(originatingElements);
        return builder;
    }

    public void writeTo(@Nonnull Appendable out) {
        try {
            JavaCodeWriter codeWriter = new JavaCodeWriter(out, indent, lineSeparator);
            emit(codeWriter);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void writeTo(Filer filer) {
        String fileName = packageName.isEmpty()
            ? typeSpec.name
            : packageName + "." + typeSpec.name;
        JavaFileObject filerSourceFile = null;
        Writer writer = null;
        try {
            filerSourceFile = filer.createSourceFile(fileName, this.originatingElements.toArray(new Element[0]));
            writer = filerSourceFile.openWriter();
            writeTo(writer);
        } catch (Exception e) {
            if (filerSourceFile != null) {
                try {

                    filerSourceFile.delete();
                } catch (Exception ignored) {
                    LOG.error("Failed to deleter filerSourceFile", e);
                }
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOG.error("Failed to close writer", e);
                }
            }
        }
    }


    /**
     * Writes this to the given {@code path} as UTF-8 using the standard java directory structure.
     *
     * @param path the given path
     */
    public void writeTo(@Nonnull Path path) {
        writeTo(path, UTF_8);
    }

    /**
     * Writes this to the given {@code path} with the given {@code charset} using standard java directory
     * structure.
     *
     * @param path    the given path
     * @param charset the charset of file
     */
    public void writeTo(@Nonnull Path path, @Nonnull Charset charset) {
        argNotNull(charset, "charset");
        path = resolveOutputPath(path, true);
        argument(path, Files::notExists, this::messageForPathExists);
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), charset)) {
            writeTo(writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Computes the whole output path of a java file.
     *
     * @param path the path to write a java file
     * @return the {@link Path} instance to which source will be written.
     */
    public Path resolveOutputPath(@Nonnull Path path) {
        return resolveOutputPath(path, false);
    }

    /**
     * Delete the java file if it exists, then return the input path directly.
     *
     * @param path the path to write a java file
     * @return the input {@code path}
     */
    public Path delete(@Nonnull Path path) {
        try {
            Path javaFilePath = resolveOutputPath(path, false);
            if (Files.exists(javaFilePath)) {
                Files.delete(javaFilePath);
            }
            return path;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Writes this to the given {@code directory} as UTF-8 using the standard java directory structure.
     *
     * @param directory the given directory
     */
    public void writeTo(@Nonnull File directory) {
        writeTo(directory, UTF_8);
    }

    /**
     * Writes this to the given {@code directory} with the given {@code charset} using standard java directory
     * structure.
     *
     * @param directory the given directory
     * @param charset   the charset of file
     */
    public void writeTo(@Nonnull File directory, @Nonnull Charset charset) {
        writeTo(argNotNull(directory, "directory").toPath(), charset);
    }

    /**
     * Computes the whole output file of java file.
     *
     * @param directory the directory to write file
     * @return the {@link File} instance to which source will be written.
     */
    public File resolveOutputFile(@Nonnull File directory) {
        return resolveOutputPath(argNotNull(directory, "directory").toPath()).toFile();
    }

    public Path resolveOutputPath(@Nonnull Path path, boolean createDirectory) {
        argNotNull(path, "path");
        argument(path, Files::exists, this::messageForPathNotExist);
        argument(path, Files::isDirectory, this::messageForPathIsNotDirectory);
        if (StringHelper.isNotBlank(packageName)) {
            for (String packagePart : packageName.split("\\.")) {
                path = path.resolve(packagePart);
            }
            if (createDirectory) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        path = path.resolve(typeSpec.name + ".java");
        return path;
    }

    private String messageForPathNotExist(Path path) {
        return String.format("%s doesn't exist.", path.toAbsolutePath());
    }

    private String messageForPathIsNotDirectory(Path path) {
        return String.format("%s isn't a directory.", path.toAbsolutePath());
    }

    private String messageForPathExists(Path path) {
        return String.format("%s exists.", path.toAbsolutePath());
    }

    private void emit(JavaCodeWriter codeWriter) throws IOException {
        //emit javadoc
        if (!javadoc.isEmpty()) {
            codeWriter.emitJavadoc(javadoc);
            codeWriter.emitNewLine();
        }

        //emit package
        codeWriter.emit("package $L;", packageName).emitNewLine();
        codeWriter.emitNewLine();

        //emit staticImports;
        List<String> staticImports = new ArrayList<>(this.typeSpec.staticImports());
        Collections.sort(staticImports);
        if (ObjectHelper.isNotEmpty(staticImports)) {
            for (String staticImport : staticImports) {
                codeWriter.emit("import static $L;", staticImport).emitNewLine();
            }
            codeWriter.emitNewLine();
        }

        //emit imports of class and file comment
        Set<String> rawImports = new HashSet<>(typeSpec.imports());
        rawImports.addAll(javadoc.imports());
        List<String> imports = new ArrayList<>();
        Set<String> alwaysQualifiedNames = new HashSet<>();
        prepareImports(rawImports, imports, alwaysQualifiedNames);
        AlwaysQualifiedNamesHolder.set(alwaysQualifiedNames);

        Character c = null;
        for (String importString : imports) {
            if (c != null && importString.charAt(0) != c) {
                codeWriter.emitNewLine();
            }
            codeWriter.emit("import $L;", importString).emitNewLine();
            c = importString.charAt(0);
        }
        codeWriter.emitNewLine();

        typeSpec.emit(codeWriter, null, false);

        //It's said that the last part of java file must be an empty line.
        codeWriter.emitNewLine();

        AlwaysQualifiedNamesHolder.clear();
    }

    /**
     * resolve class name clash
     * to remove redundant imports: java.lang or in the same package of the current class.
     * sort imports
     */
    protected void prepareImports(Set<String> rawImports, List<String> imports, Set<String> alwaysQualifiedNames) {
        Map<String, Set<String>> map = new HashMap<>(); // name -> a set of importString with the same name.
        Map<String, String> packageMap = new HashMap<>(); // importString -> packageName
        Map<String, String> nameMap = new HashMap<>();// importString -> name
        collectTypeNames(packageMap, nameMap, this.typeSpec, null); // prevent conflict with current type spec or its inner types.
        for (String importString : rawImports) {
            int index = importString.lastIndexOf(".");
            String name;
            String packageName = "";
            if (index == -1) {
                name = importString;
            } else {
                name = importString.substring(index + 1);
                packageName = importString.substring(0, index);
            }
            packageMap.put(importString, packageName);
            nameMap.put(importString, name);
            Set<String> set = map.computeIfAbsent(name, k -> new HashSet<>());
            set.add(importString);
        }
        List<String> tempImports = new ArrayList<>();
        map.values().forEach(v -> LogicOptional.of(v).ifElse(l -> l.size() > 1, l -> l.stream().map(nameMap::get).forEach(alwaysQualifiedNames::add), tempImports::addAll));
        tempImports.stream().filter(i -> !(packageMap.get(i).equals("java.lang") || packageMap.get(i).equals(this.packageName))).forEach(imports::add);
        Collections.sort(imports);
    }

    private void collectTypeNames(Map<String, String> packageMap, Map<String, String> nampMap, TypeSpec typeSpec, String parent) {
        nampMap.put(typeSpec.name, parent == null ? this.packageName + "." + typeSpec.name : this.packageName + "." + parent + "." + typeSpec.name);
        packageMap.put(typeSpec.name, this.packageName);
        typeSpec.innerTypes.forEach(innerType -> collectTypeNames(packageMap, nampMap, innerType, parent == null ? typeSpec.name : parent + "." + typeSpec.name));
    }


    public static final class Builder {
        final List<Element> originatingElements = new ArrayList<>();
        private final String packageName;
        private final TypeSpec typeSpec;
        private FileJavadoc javadoc = FileJavadoc.builder().build();
        private String indent = StringHelper.repeatChar(' ', 4);
        private String lineSeparator = "\n";

        private Builder(String packageName, TypeSpec typeSpec) {
            //TODO:校验package
            this.packageName = packageName;
            this.typeSpec = argNotNull(typeSpec, "typeSpec");
            this.originatingElements.addAll(typeSpec.originatingElements);
        }

        public Builder addOriginatingElement(Element originatingElement) {
            this.originatingElements.add(argNotNull(originatingElement, "originatingElement"));
            return this;
        }

        public Builder addOriginatingElements(List<Element> originatingElements) {
            this.originatingElements.addAll(argNotNull(originatingElements, "originatingElements"));
            return this;
        }

        public Builder setIndent(String indent) {
            this.indent = argNotNull(indent, "indent");
            return this;
        }

        public Builder setLineSeparator(String lineSeparator) {
            this.lineSeparator = argNotNull(lineSeparator, "lineSeparator");
            return this;
        }

        public Builder setJavadoc(FileJavadoc javadoc) {
            this.javadoc = argNotNull(javadoc, "javadoc");
            return this;
        }

        public Builder setJavadoc(String format, Object... args) {
            return setJavadoc(FileJavadoc.builder().beginJavadoc().addDoc(format, args).end().build());
        }

        public Builder setJavadoc(JavaCodeBlock codeBlock) {
            return setJavadoc(FileJavadoc.builder().beginJavadoc().addDoc(codeBlock).end().build());
        }

        public Builder setJavadoc(Function<FileJavadoc.Builder, FileJavadoc> function) {
            setJavadoc(function.apply(FileJavadoc.builder()));
            return this;
        }

        public Builder setNamedJavadoc(String format, Map<String, ?> args) {
            return setJavadoc(FileJavadoc.builder().beginJavadoc().addNamedDoc(format, args).end().build());
        }

        public JavaFile build() {
            return new JavaFile(this);
        }

    }

}
