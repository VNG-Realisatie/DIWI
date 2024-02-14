package nl.vng.diwi.generic;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtil
{
    private final static Logger logger = LogManager.getLogger();

    /**
     * Retrieves a list of all files found at the path specified in searchPath
     * @param searchPath
     * @param filter Allows to specify a filter to filter files by. Can be null.
     * @param addDirs Whether to include dirs in the returned result.
     * @throws IOException
     */
    public static List<File> getAllFiles(Path searchPath, FileFilter filter, boolean addDirs) throws IOException
    {
        if (!searchPath.toFile().exists())
        {
            throw new FileNotFoundException(
                    "Search dir '" + searchPath.toString() + "' not found");
        }
        List<File> filelist = getDirContents(filter, searchPath.toFile(), addDirs);
        return filelist;

    }

    /**
     * Retrieves a list of all files found by recursing the path specified in searchPath
     * @param searchPath
     * @param filter Allows to specify a filter to filter files by
     * @param addDirs Whether to include dirs in the returned result
     * @throws IOException
     */
    public static List<File> getAllFilesRecursive(Path searchPath, FileFilter filter, boolean addDirs) throws IOException
    {
        List<File> filelist = new ArrayList<File>();
        if (!searchPath.toFile().exists())
        {
            throw new FileNotFoundException(
                    "Search dir '" + searchPath.toString() + "' not found");
        }
        recurseDirContents(filelist, filter, searchPath.toFile(), addDirs);
        return filelist;

    }

    private static void recurseDirContents(List<File> inputList, FileFilter filter, File dir, boolean addDirs) throws IOException
    {
        File[] files = dir.listFiles(filter);
        for (File file : files)
        {
            if (file.isDirectory())
            {
                if (addDirs)
                {
                    inputList.add(file);
                }
                recurseDirContents(inputList, filter, file, addDirs);
            }
            else
            {
                inputList.add(file);
            }
        }
    }

    private static List<File> getDirContents(FileFilter filter, File dir, boolean addDirs) throws IOException
    {
        List<File> fileList = new ArrayList<File>();
        File[] files = dir.listFiles(filter);
        for (File file : files)
        {
            if (file.isDirectory())
            {
                if (addDirs)
                {
                    fileList.add(file);
                }
            }
            else
            {
                fileList.add(file);
            }
        }
        return fileList;
    }

    public static String getFileNameWithoutExtension(File file)
    {
        String filename = file.getName();
        String[] parts = filename.split("\\.");
        if (parts.length > 0)
        {
            return parts[0];
        }
        else
        {
            return null;
        }
    }

    public static String getExtension(File file)
    {
        String filename = file.getName();
        String[] parts = filename.split("\\.");
        if (parts.length > 0)
        {
            String extension = "";
            for (int i = 1; i < parts.length; ++i)
            {
                extension += parts[i] + ".";
            }
            return extension.substring(0, extension.length() - 1);
        }
        else
        {
            return null;
        }
    }

    public static File UrlAsFile(URL url) throws UrlNotAFileUrlException
    {
        String urlString = url.toString();
        if ((urlString.length() >= 6) && (urlString.substring(0, 6).equals("file:/")))
        {
            return new File(urlString.substring(5));
        }
        else
        {
            throw new UrlNotAFileUrlException();
        }
    }

    public static class UrlNotAFileUrlException extends Exception
    {
        private static final long serialVersionUID = -2116031915041216441L;
    }

    public static void recursiveDelete(Path path) throws IOException
    {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException
            {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
            {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                if (exc == null)
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
                else
                {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
    }

    /**
     * Combines paths by adding a / if needed in between.
     *
     * @param pathLeft
     * @param pathRight
     */
    public static String combinePaths(String pathLeft, String pathRight)
    {
        String newPathRight = pathRight;
        if (pathLeft.endsWith("/"))
        {
            if (pathRight.startsWith("/"))
            {
                newPathRight = newPathRight.substring(1);
            }
        }
        else
        {
            if ((!pathRight.startsWith("/")) && (pathLeft.length() > 0))
            {
                newPathRight = "/" + newPathRight;
            }
        }
        return pathLeft + newPathRight;
    }

    public static InputStream getPossibleZipStream(File jsonFile) throws IOException
    {
        if (jsonFile.toString().endsWith(".gz"))
        {
            FileInputStream instream = new FileInputStream(jsonFile);
            GZIPInputStream ginstream = new GZIPInputStream(instream);
            return ginstream;
        }
        else if (jsonFile.toString().endsWith(".zip"))
        {
            InputStream inputStream = new FileInputStream(jsonFile);
            ZipInputStream zipStream = new ZipInputStream(inputStream);
            ZipEntry entry = zipStream.getNextEntry(); // fetch the next (=first) file
            if (entry.getMethod() != 8)
            {
                logger.warn("ZipInputStream only supports archives compressed using zip -6. For complaints, call Oracle");
            }
            logger.info("Extracting " + (entry.isDirectory() ? "directory " : "file ") + entry.getName());
            return zipStream;
        }
        else
        {
            InputStream inputStream = new FileInputStream(jsonFile);
            return inputStream;
        }
    }

    public static List<File> getStringsAsFiles(List<String> filePaths)
    {
        return filePaths.stream().map(path -> new File(path)).collect(Collectors.toList());
    }
}
