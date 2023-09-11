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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Replacer
{
    private final Dotenv envSource;
    private final String prefix;

    public Replacer(Dotenv envSource, String prefix)
    {
        this.envSource = envSource;
        this.prefix = prefix;
    }

    public Result replace(Path path, String in)
    {
        int replacements = 0;
        List<String> missingVariables = new ArrayList<>();
        Matcher matcher = VAR_PATTERN.matcher(in);
        StringBuilder sb = new StringBuilder();

        while(matcher.find())
        {
            String varName = matcher.group(1);
            if(!prefix.isEmpty() && !varName.startsWith(prefix))
                continue;

            String value = envSource.get(varName);
            if(value == null)
            {
                missingVariables.add(varName);
                continue;
            }
            else
            {
                ++replacements;
                LOGGER.info("Found placeholder {} in {}", varName, path);
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }

        matcher.appendTail(sb);
        return new Result(replacements, missingVariables, sb.toString());
    }

    public record Result(int replacements, List<String> missingVariables, String out)
    {
    }

    private static final Logger LOGGER = LogManager.getRootLogger();
    private static final Pattern VAR_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
}
