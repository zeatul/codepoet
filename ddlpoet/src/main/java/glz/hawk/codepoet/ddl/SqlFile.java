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

package glz.hawk.codepoet.ddl;

import glz.hawk.codepoet.ddl.dialect.Dialect;
import glz.hawkframework.core.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class SqlFile {
    public final DatabaseCodeBlock fileComment;
    public final DatabaseSpec databaseSpec;
    public final String packageName;
    private final Logger LOG = LoggerFactory.getLogger(SqlFile.class);
    private final String indent;
    private final String lineSeparator;
    private final Dialect dialect;
    private final List<Element> originatingElements;
    private final String filename;

    private SqlFile(Builder builder) {
        this.fileComment = builder.fileCommentBuilder.build();
        this.databaseSpec = builder.databaseSpec;
        this.indent = builder.indent;
        this.lineSeparator = builder.lineSeparator;
        this.dialect = builder.dialect;
        this.packageName = builder.packageName;
        this.originatingElements = builder.originatingElements;
        this.filename = builder.filename;
    }

    public Builder toBuilder() {
        return new Builder(databaseSpec, dialect)
            .setFilename(filename)
            .setIndent(indent)
            .setPackage(packageName)
            .setLineSeparator(lineSeparator)
            .addFileComment(fileComment)
            .addOriginatingElements(originatingElements);
    }

    public static Builder builder(DatabaseSpec databaseSpec, Dialect dialect) {
        return new Builder(databaseSpec, dialect);
    }

    public void writeTo(Appendable out) {
        DatabaseCodeWriter codeWriter = new DatabaseCodeWriter(out, dialect, indent, lineSeparator);
        emit(codeWriter);
    }

    public void writeTo(Path path) {
        argNotNull(path, "path");
        path = resolveOutputPath(path, true);
        argument(path, Files::notExists, this::messageForPathExists);
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            writeTo(writer);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public void writeTo(File directory) {
        writeTo(argNotNull(directory, "directory").toPath());
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
        path = path.resolve(filename);
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

    public void writeTo(Filer filer) {
        FileObject filerSourceFile = null;
        Writer writer = null;
        try {
            argNotBlank(packageName, packageName);
            argNotBlank(filename, "filename");
            filerSourceFile = filer.createResource(StandardLocation.CLASS_OUTPUT, packageName, filename, this.originatingElements.toArray(new Element[0]));
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

    private void emit(DatabaseCodeWriter codeWriter) {
        try {
            if (!fileComment.isEmpty()) {
                codeWriter.emit(fileComment);
                codeWriter.emitNewLine();
            }
            databaseSpec.emit(codeWriter);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static final class Builder {
        private final DatabaseSpec databaseSpec;
        private final Dialect dialect;
        private final DatabaseCodeBlock.Builder fileCommentBuilder = DatabaseCodeBlock.builder();
        private final List<Element> originatingElements = new ArrayList<>();
        private String indent = StringHelper.repeatChar(' ', 4);
        private String lineSeparator = "\n";
        private String packageName;
        private String filename;

        private Builder(DatabaseSpec databaseSpec, Dialect dialect) {
            this.databaseSpec = argNotNull(databaseSpec, "databaseSpec");
            this.dialect = argNotNull(dialect, "dialect");
        }

        public Builder addFileComment(String format, Object... args) {
            this.fileCommentBuilder.add(format, args);
            return this;
        }

        public Builder addFileComment(DatabaseCodeBlock codeBlock) {
            this.fileCommentBuilder.add(codeBlock);
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

        public Builder setPackage(String packageName) {
            this.packageName = argNotBlank(packageName, "packageName");
            return this;
        }

        public Builder addOriginatingElement(Element originatingElement) {
            this.originatingElements.add(argNotNull(originatingElement, "originatingElement"));
            return this;
        }

        public Builder addOriginatingElements(Element... originatingElements) {
            addOriginatingElements(Arrays.asList(originatingElements));
            return this;
        }

        public Builder addOriginatingElements(Iterable<Element> originatingElements) {
            int index = -1;
            for (Element element : argNotNull(originatingElements, "originatingElements")) {
                this.originatingElements.add(argElementNotNull(element, ++index, "originatingElements"));
            }
            return this;
        }

        public Builder setFilename(String filename) {
            this.filename = argNotBlank(filename, "filename");
            return this;
        }

        public SqlFile build() {
            //TODO:校验
            return new SqlFile(this);
        }
    }
}
