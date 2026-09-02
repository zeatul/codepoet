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
import java.util.*;

import static glz.hawkframework.core.support.ArgumentSupport.*;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class PropertiesFile {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesFile.class);
    public final String packageName;
    public final Locale locale;
    public final Properties properties;
    public final String filename;
    private final List<Element> originatingElements;
    private final String lineSeparator;

    private PropertiesFile(Builder builder) {
        this.packageName = builder.packageName;
        this.locale = builder.locale;
        this.properties = builder.properties;
        this.filename = builder.filename;
        this.originatingElements = builder.originatingElements;
        this.lineSeparator = builder.lineSeparator;
    }

    public Builder toBuilder() {
        return new Builder(packageName, locale, properties)
            .setFilename(filename)
            .setOriginatingElements(originatingElements)
            .setLineSeparator(lineSeparator);
    }

    public static Builder builder(String packageName, Locale locale, Properties properties) {
        return new Builder(packageName, locale, properties);
    }

    public static Builder builder(String packageName, Locale locale) {
        return new Builder(packageName, locale);
    }

    private void write(Writer writer, Properties properties) throws IOException {
        List<String> keys = new ArrayList<>(properties.stringPropertyNames());
        for (int i=0; i<keys.size(); i++){
            String key = keys.get(i);
            writer.write(key + "=" + properties.getProperty(key));
            if (i<keys.size()-1) writer.write(lineSeparator);
        }
    }


    public void writeTo(Filer filer) {
        argNotBlank(packageName, packageName);
        String outputFileName = getOutputFileName();
        FileObject filerSourceFile = null;
        Writer writer = null;
        try {
            filerSourceFile = filer.createResource(StandardLocation.CLASS_OUTPUT, packageName, outputFileName, this.originatingElements.toArray(new Element[0]));
            writer = filerSourceFile.openWriter();
            write(writer, properties);
        } catch (Exception e) {
            if (filerSourceFile != null) {
                try {
                    filerSourceFile.delete();
                } catch (Exception ignored) {
                    LOG.error("Failed to deleter filerSourceFile", e);
                }
            }
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
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

    public void writeTo(File directory) {
        writeTo(argNotNull(directory, "directory").toPath());
    }

    public void writeTo(Path path) {
        argNotNull(path, "path");
        path = resolveOutputPath(path, true);
        argument(path, Files::notExists, this::messageForPathExists);
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            write(writer, properties);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private Path resolveOutputPath(@Nonnull Path path, boolean createDirectory) {
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
        path = path.resolve(getOutputFileName());
        return path;
    }

    private String messageForPathNotExist(Path path) {
        return String.format("%s doesn't exist.", path.toAbsolutePath());
    }

    private String messageForPathExists(Path path) {
        return String.format("%s exists.", path.toAbsolutePath());
    }

    private String messageForPathIsNotDirectory(Path path) {
        return String.format("%s isn't a directory.", path.toAbsolutePath());
    }

    private String getOutputFileName() {
        argNotBlank(filename, "filename");
        StringBuilder sb = new StringBuilder(filename);
        if (locale != null) {
            sb.append("_").append(locale.toString().replace("-", "_"));
        }
        sb.append(".properties");
        return sb.toString();
    }

    public static final class Builder {
        private final String packageName;
        private final Locale locale;
        private final Properties properties;
        private String filename = "enum_display";
        private List<Element> originatingElements = Collections.emptyList();
        private String lineSeparator = "\n";

        public Builder(String packageName, Locale locale) {
            this(packageName, locale, new Properties());
        }

        public Builder(String packageName, Locale locale, Properties properties) {
            this.packageName = argNotBlank(packageName, "packageName");
            this.locale = argNotNull(locale, "locale");
            this.properties = argNotNull(properties, "properties");
        }

        public Builder setFilename(String filename) {
            this.filename = argNotBlank(filename, "filename");
            return this;
        }

        public Builder setOriginatingElements(List<Element> originatingElements) {
            this.originatingElements = argNotNull(originatingElements, "originatingElements");
            return this;
        }

        public Builder setLineSeparator(String lineSeparator) {
            this.lineSeparator = argNotNull(lineSeparator, "lineSeparator");
            return this;
        }

        public PropertiesFile build() {
            return new PropertiesFile(this);
        }
    }
}
