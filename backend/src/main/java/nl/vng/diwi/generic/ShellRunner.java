package nl.vng.diwi.generic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShellRunner
{
    private static Logger logger = LogManager.getLogger();

    public static class ShellException extends RuntimeException
    {
        private static final long serialVersionUID = -6974765531193022594L;

        public ShellException(String message)
        {
            super(message);
        }
    }

    public static void runChecked(final String setupTables)
    {
        int exitCode = runAndPrintSimpleCommand(setupTables);
        if (exitCode != 0)
        {
            throw new ShellException("'" + setupTables + "' returned with exit code " + exitCode);
        }
    }

    public static int runAndPrintSimpleCommand(String shellCommand)
    {
        return runAndPrintSimpleCommand(shellCommand, 30);
    }

    public static int runAndPrintSimpleCommand(String shellCommand, int timeoutSeconds)
    {
        try
        {
            final int msStep = 50;

            logger.warn("{}: ", shellCommand);

            ProcessBuilder pb = new ProcessBuilder("sh", "-c", shellCommand);
            pb.redirectErrorStream(true);
            Process prs = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(prs.getInputStream()));
            String line = null;
            final int timeOutMilliseconds = timeoutSeconds * 1000;
            int milliseconds = 0;
            while (milliseconds < timeOutMilliseconds)
            {
                prs.waitFor(msStep, TimeUnit.MILLISECONDS);
                while (in.ready())
                {
                    line = in.readLine();
                    logger.warn(line);
                }
                if (!prs.isAlive())
                {
                    break;
                }
                milliseconds += msStep;
            }
            if (milliseconds >= timeOutMilliseconds)
            {
                throw new ShellException("Shell script took longer than specified timeout, " + (milliseconds / 1000) + " seconds.");
            }
            return prs.exitValue();
        }
        catch (Exception e)
        {
            ShellException se = new ShellException(e.getMessage());
            se.addSuppressed(e);
            throw se;
        }
    }

    public static int runSimpleCommand(String shellCommand, StringBuilder output, int timeoutSeconds)
    {
        try
        {
            logger.warn (shellCommand);
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", shellCommand);
            pb.redirectErrorStream(true);
            Process prs = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(prs.getInputStream()));
            String line = null;
            int seconds = 0;
            while (seconds < timeoutSeconds)
            {
                prs.waitFor(1, TimeUnit.SECONDS);
                // System.out.print(".");
                while (in.ready())
                {
                    line = in.readLine();
                    logger.warn (line);
                    // System.out.println(line);
                    output.append(line + "\n");
                }
                if (!prs.isAlive())
                {
                    break;
                }
                ++seconds;
            }
            // System.out.println("");
            if (seconds >= timeoutSeconds)
            {
                throw new ShellException("Shell script took longer than specified timeout, " + seconds + " seconds.");
            }
            return prs.exitValue();
        }
        catch (Exception e)
        {
            ShellException se = new ShellException(e.getMessage());
            se.addSuppressed(e);
            throw se;
        }
    }
}
