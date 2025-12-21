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

package glz.hawk.codepoet.project;

import glz.hawkframework.core.support.ArgumentSupport;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * This class provides some utility methods for a project path.
 *
 * @author Hawk
 */
public abstract class ProjectHelper {

    public static Path rootProjectPath() {
        return Paths.get("").toAbsolutePath();
    }

    public static Path subProjecPath(String... subDirectoryNames) {
        Path path = rootProjectPath();
        for (String subDirectoryName : subDirectoryNames) {
            path = path.resolve(subDirectoryName);
        }
        return path;
    }

    public static Path srcMainJava(Path projectPath) {
        return projectPath.resolve("src").resolve("main").resolve("java");
    }

    public static Path srcTestJava(Path projectPath) {
        return projectPath.resolve("src").resolve("test").resolve("java");
    }

    public static Path srcGeneratedJava(Path projectPath) {
        return projectPath.resolve("src").resolve("generated").resolve("java");
    }

    public static Path srcGeneratedResources(Path projectPath) {
        return projectPath.resolve("src").resolve("generated").resolve("resources");
    }

    public static Path srcGeneratedTestJava(Path projectPath) {
        return projectPath.resolve("src").resolve("generatedTest").resolve("java");
    }

    public static Path srcGeneratedTestResources(Path projectPath) {
        return projectPath.resolve("src").resolve("generatedTest").resolve("resources");
    }

    public static Path pathForPackage(Path path, String packageName) {
        for (String part : packageName.split("\\.")) {
            path = path.resolve(part);
        }
        return path;
    }

    public static Path pathFor(Path path, String subPath) {
        for (String part : subPath.split("/")) {
            path = path.resolve(part);
        }
        return path;
    }

    public static Path pathFor(Path path, String subPath, String packageName) {
        return pathForPackage(pathFor(path, subPath), packageName);
    }

    /**
     * Clears all the files in the path except subdirectories and files in the subdirectories.
     */
    public static void clearFiles(Path path) {
        ArgumentSupport.argument(path, Files::isDirectory, p -> String.format("The path: %s is not a directory", p));
        try (Stream<Path> entries = Files.list(path)) {
            entries.filter(Files::isRegularFile).forEach(ProjectHelper::deleteFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
