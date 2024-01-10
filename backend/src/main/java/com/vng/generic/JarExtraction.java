package com.vng.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarExtraction
{
    public void extractDir(Object classInContainingJar, String path, File destinationDir) throws IOException
    {
        final File jarFile = new File(classInContainingJar.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        List<String> files = listFilesInJar(jarFile, path);

        if(!destinationDir.exists() || !destinationDir.isDirectory())
        {
            throw new FileNotFoundException("Destination directory for Jar extraction does not exist or is not a directory");
        }

        for(String file : files)
        {
            InputStream inStream = null;
            File targetFile = null;

            if(jarFile.isFile()) //in a jar
            {
                inStream = getClass().getResourceAsStream("/" + file);
                if(inStream == null) //check if directory
                {
                    continue;
                }

            }
            else //in IDE / Maven
            {
               File readFile = new File(file);
               if(readFile.isDirectory())
               {
                   continue;
               }
               inStream = new FileInputStream(readFile);

               int startIndex = file.indexOf("classes/" + path);
               if(startIndex > 0)
               {
                   startIndex += 8; //look beyond the classes/ part
                   file = file.substring(startIndex);
               }
               else
               {
                   inStream.close();
                   throw new FileNotFoundException("Linked file outside scope of resources");
               }


            }

            targetFile = new File(destinationDir.getPath() + "/" + file);
            targetFile.getParentFile().mkdirs();
            Files.copy(inStream, targetFile.toPath());
            inStream.close();
        }
    }

    public static InputStream getResourceAsStream(Object classInContainingJar, String resourcePath) throws FileNotFoundException
    {
        final File jarFile = new File(classInContainingJar.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        InputStream inStream;
        if(jarFile.isFile()) //in a jar
        {
            inStream = classInContainingJar.getClass().getResourceAsStream("/" + resourcePath);
        }
        else //in IDE / Maven
        {
            inStream = new FileInputStream("src/main/resources/" + resourcePath);
        }
        return inStream;
    }

    private List<String> listFilesInJar(File jarFile, String path) throws IOException
    {
        List<String> filesInJar = new ArrayList<String>();

        if (jarFile.isFile())
        { // Run with JAR file
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries(); // gives ALL
                                                                 // entries in
                                                                 // jar
            while (entries.hasMoreElements())
            {
                JarEntry element = entries.nextElement();
                if(!element.isDirectory())
                {
                    final String name = element.getName();
                    if (name.startsWith(path + "/"))
                    { // filter according to the path
                        filesInJar.add(name);
                    }
                }
            }
            jar.close();
        }
        else
        { // Run with IDE / Maven
            URL url = JarExtraction.class.getResource("/" + path);
            if (url != null)
            {
                try
                {
                    String urlString = url.toString();
                    urlString.replaceAll("%", "&#37;");
                    url = new URL(urlString);
                    final File apps = new File(url.toURI());
                    List<File> filesFound = FileUtil.getAllFilesRecursive(apps.toPath(), new RegexFileFilter(".*"), false);
                    for (File file : filesFound)
                    {
                        filesInJar.add(file.getPath());
                    }
                }
                catch (URISyntaxException ex)
                {
                    // never happens
                }
            }
        }
        return filesInJar;
    }
}
