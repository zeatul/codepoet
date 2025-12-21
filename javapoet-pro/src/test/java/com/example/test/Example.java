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

package com.example.test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class Example {

    public List<String> readText(File file, String charset) {
        FileInputStream fis = null;
        try {
            List<String> lines = new ArrayList<>();
            fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, charset));
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
            return lines;
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }

    public byte[] readFile(File file){
        try(FileInputStream fileInputStream = new FileInputStream((file))){
            byte[] b = new byte[Integer.parseInt(String.valueOf(file.length()))];
            int result = fileInputStream.read(b);
            if (result == -1){
                throw new RuntimeException("Read no data");
            }
            return b;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
