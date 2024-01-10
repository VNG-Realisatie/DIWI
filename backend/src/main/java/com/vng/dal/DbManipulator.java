package com.vng.dal;

import java.io.File;
import java.nio.file.Files;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.vng.generic.FileUtil;
import com.vng.generic.JarExtraction;
import com.vng.generic.ShellRunner;
import com.vng.generic.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DbManipulator implements AutoCloseable
{
    // should have trailing slash
    private static final String gitBashScriptsLoc = "shellScripts/repoBin/";
    private File tempDir;

    public static class DbInfo
    {
        public String dbName;
        public String dbUser;
        public String dbPassword;
        public String dbUrl;
    }

    public DbManipulator()
    {
        try
        {
            JarExtraction je = new JarExtraction();
            tempDir = Files.createTempDirectory("ormScripts").toFile();
            je.extractDir(this, "shellScripts", tempDir);
            StringBuilder output = new StringBuilder();
            ShellRunner.runSimpleCommand("chmod +x " + tempDir.getAbsolutePath() + "/" + gitBashScriptsLoc + "/*.sh",
                                         output,
                                         10);
        }
        catch (Exception e)
        {
            DbOperationFailedException de = new DbOperationFailedException(e.getMessage());
            de.addSuppressed(e);
            throw de;
        }
    }

    @Override
    public void close()
    {
        try
        {
            if ((tempDir != null) && (tempDir.exists()))
            {
                FileUtil.recursiveDelete(tempDir.toPath());
            }
        }
        catch (Exception e)
        {
            throw new DbOperationFailedException(e.getMessage(), e);
        }
    }

    private String getScriptLoc() throws FileUtil.UrlNotAFileUrlException
    {
        String ret = tempDir.getAbsolutePath() + "/" + gitBashScriptsLoc;
        return ret;
    }

    public static void dropDb(String dbName)
    {
        try
        {
            final String shellCommand = "dropdb " + dbName;
            int exitCode = ShellRunner.runAndPrintSimpleCommand(shellCommand);
            if (exitCode != 0)
            {
                throw new DbOperationFailedException("'" + shellCommand + "' return with exit code " + exitCode);
            }
        }
        catch (Exception e)
        {
            throw new DbOperationFailedException(e.getMessage(), e);
        }
    }

    public void autoDropDb(String dbName)
    {
        try
        {
            String scriptLoc = getScriptLoc();
            int exitCode = ShellRunner.runAndPrintSimpleCommand(scriptLoc + "autoDrop.sh " + dbName);
            if (exitCode != 0)
            {
                throw new DbOperationFailedException("autoDrop return with exit code " + exitCode);
            }
        }
        catch (Exception e)
        {
            DbOperationFailedException de = new DbOperationFailedException(e.getMessage());
            de.addSuppressed(e);
            throw de;
        }
    }

    public static void createEmptyDbStatic(String dbName, String dbUser)
    {
        try
        {
            final String createDbCommand = MessageFormat.format("createdb -O {0} {1}", dbUser, dbName);
            ShellRunner.runChecked(createDbCommand);
        }
        catch (Exception e)
        {
            throw new DbOperationFailedException(e.getMessage(), e);
        }
    }

    public void createEmptyDb(String dbName, String dbUser)
    {
        createEmptyDbStatic(dbName, dbUser);
    }

    public boolean isDbPresent(String dbName)
    {
        try
        {
            String scriptLoc = getScriptLoc();
            int exitCode = ShellRunner.runAndPrintSimpleCommand(scriptLoc + "hasDb.sh " + dbName);
            return exitCode == 0;
        }
        catch (Exception e)
        {
            throw new DbOperationFailedException(e.getMessage(), e);
        }
    }

    public String getDbUser(String dbName)
    {
        try
        {
            String scriptLoc = getScriptLoc();
            StringBuilder output = new StringBuilder();
            int exitCode = ShellRunner.runSimpleCommand(scriptLoc + "getDbUser.sh " + dbName, output, 10);
            if (exitCode != 0)
            {
                throw new DbOperationFailedException("getDbUser return with exit code " + exitCode);
            }

            return StringUtil.getFirstLineFromString(output.toString());
        }
        catch (Exception e)
        {
            throw new DbOperationFailedException(e.getMessage(), e);
        }
    }

    public DbInfo getDbInfoFromDalConfig(DalConfig dalConfig) throws DbOperationFailedException
    {
        DbInfo dbInfo = getDbInfoFromHibernateFile(new File(dalConfig.ormConfigFile));
        if (dalConfig.dbName != null)
        {
            dbInfo.dbName = dalConfig.dbName;
        }
        return dbInfo;
    }

    public DbInfo getDbInfoFromHibernateFile(File hibernateFile) throws DbOperationFailedException
    {
        return getDbInfoFromHibernateFileStatic(hibernateFile);
    }

    public static DbInfo getDbInfoFromHibernateFileStatic(File hibernateFile) throws DbOperationFailedException
    {
        try
        {
            DbInfo ret = new DbInfo();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(hibernateFile);

            NodeList properties = document.getElementsByTagName("property");
            for (int i = 0; i < properties.getLength(); ++i)
            {
                Node node = properties.item(i);
                String nodeName = node.getAttributes().getNamedItem("name").getNodeValue();
                if (nodeName.equalsIgnoreCase("hibernate.connection.username"))
                {
                    ret.dbUser = node.getTextContent();
                }
                else if (nodeName.equalsIgnoreCase("hibernate.connection.password"))
                {
                    ret.dbPassword = node.getTextContent();
                }
                else if (nodeName.equalsIgnoreCase("hibernate.connection.url"))
                {
                    ret.dbUrl = node.getTextContent();
                }
            }
            if ((ret.dbUrl != null) && (ret.dbUrl.length() > 0))
            {
                int endUrlIndex = ret.dbUrl.lastIndexOf("/");
                if (endUrlIndex > 0)
                {
                    ret.dbName = ret.dbUrl.substring(endUrlIndex + 1);
                }
            }
            return ret;
        }
        catch (Exception e)
        {
            throw new DbOperationFailedException(e.getMessage(), e);
        }
    }

    public void dropAndImportDbFromFile(String dbName, String dbUser, File filename)
    {
        try
        {
            String scriptLoc = getScriptLoc();
            String command = String.format("%sdropAndImportDb.sh '%s' '%s' '%s'", scriptLoc, dbName, dbUser, filename.toString());
            int exitCode = ShellRunner.runAndPrintSimpleCommand(command);
            if (exitCode != 0)
            {
                throw new DbOperationFailedException("dropAndImportDb return with exit code " + exitCode);
            }
        }
        catch (FileUtil.UrlNotAFileUrlException e)
        {
            throw new DbOperationFailedException(e.getMessage(), e);
        }
    }

    private static class DbOperationFailedException extends RuntimeException
    {
        private static final long serialVersionUID = -7726621370209960574L;

        public DbOperationFailedException(String message)
        {
            super(message);
        }

        public DbOperationFailedException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }

    public static void enablePostGis(String dbName)
    {
        String command = String.format("psql -d '%s' -c 'CREATE EXTENSION postgis;'", dbName);
        ShellRunner.runAndPrintSimpleCommand(command);
    }

    public static boolean runSql(String db, File sqlFile)
    {
        String command = "psql -d " + db + " < '" + sqlFile.getAbsolutePath() + "'";
        return ShellRunner.runAndPrintSimpleCommand(command) == 0;
    }
}
