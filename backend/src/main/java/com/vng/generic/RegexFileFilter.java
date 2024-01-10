package com.vng.generic;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexFileFilter implements FileFilter
{
    private Pattern regexPattern;

    public RegexFileFilter(String regex)
    {
        regexPattern = Pattern.compile(regex);
    }

    @Override
    public boolean accept(File pathname)
    {
        Matcher m = regexPattern.matcher(pathname.toString());

        boolean matches = m.matches();
        //System.out.println(pathname.toString() + " matches " + regexPattern.pattern() + " = " + matches);
        return matches;
    }

}
