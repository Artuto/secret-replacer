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

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@CommandLine.Command(name = "Replace")
public class Processor implements Runnable
{
    @CommandLine.Parameters(defaultValue = ".")
    List<Path> directories;

    @CommandLine.ArgGroup(exclusive = false)
    ReplacerOptions options = new ReplacerOptions();

    @Override
    public void run()
    {
        Dotenv envSource = Dotenv.configure().load();
        Replacer replacer = new Replacer(envSource, options.prefix);

        for(Path path : directories)
        {
            if(!Files.isDirectory(path))
            {
                LOGGER.error("{} is not a directory!", path);
                return;
            }

            try
            {
                LOGGER.debug("Processing directory {} ...", path);
                processDirectory(path, replacer);
            }
            catch(Exception e)
            {
                throw new RuntimeException("Failed to process directory " + path, e);
            }
        }
    }

    private void processDirectory(Path directory, Replacer replacer) throws IOException
    {
        try(Stream<Path> paths = Files.walk(directory))
        {
            paths.filter(Files::isRegularFile)
                    .filter(options::matches)
                    .forEach(path -> processFile(path, replacer));
        }
    }

    private void processFile(Path path, Replacer replacer)
    {
        LOGGER.debug("Processing file {} ...", path);

        try
        {
            String in = Files.readString(path);
            Replacer.Result result = replacer.replace(path, in);

            if(result.replacements() > 0)
            {
                Files.writeString(path, result.out());
                LOGGER.info("Replaced {} placeholders in {}", result.replacements(), path);
            }

            if(!result.missingVariables().isEmpty())
                LOGGER.warn("Missing variables not present in dotenv: {}", result.missingVariables());
        }
        catch(IOException e)
        {
            throw new RuntimeException("Failed to process file " + path, e);
        }
    }

    public static final Logger LOGGER = LogManager.getLogger(Processor.class);
}
