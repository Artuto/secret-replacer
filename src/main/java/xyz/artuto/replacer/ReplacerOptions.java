/*
 * MIT License
 *
 * Copyright (c) 2023 Artuto
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.artuto.replacer;

import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;

public class ReplacerOptions
{
    @CommandLine.Option(names = "--replacer-env-prefix", defaultValue = "",
            description = "Sets the prefix to limit what placeholders to replace")
    String prefix;

    @CommandLine.Option(names = "--replacer-env-excludes", split = ",|\\s", splitSynopsisLabel = ",",
            description = "Files that will be excluded from processing")
    List<Path> excludes;

    @CommandLine.Option(names = "--replacer-env-exclude-paths", split = ",|\\s", splitSynopsisLabel = ",",
            description = "Directories that will be excluded from processing")
    List<Path> excludePaths;

    @CommandLine.Option(names = "--replacer-env-file-types", split = ",|\\s", splitSynopsisLabel = ",",
            defaultValue = "yml,yaml,json,properties",
            description = "File type extensions that will be processed (without the dot)")
    List<String> fileTypes;

    public boolean matches(Path path)
    {
        if(excludes != null && excludes.stream().anyMatch(other -> path.getFileName().equals(other)))
            return false;
        if(excludePaths != null && excludePaths.stream().anyMatch(path::startsWith))
            return false;
        return fileTypes != null && fileTypes.stream().anyMatch(type -> path.getFileName().toString().endsWith("." + type));
    }
}
