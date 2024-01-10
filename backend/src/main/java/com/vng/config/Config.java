package com.vng.config;

import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config
{
    private static Logger logger = LogManager.getLogger();

    public static String getConfigPath(ServletContext context)
    {
        Map<String, String> env = System.getenv();

        String configPath;
        final String configPathKey = "configpath";
        if (env.containsKey(configPathKey))
        {
            configPath = env.get(configPathKey);
            logger.info("Setting config path from env");
        }
        else if (context != null)
        {
            final String initParameter = context.getInitParameter(configPathKey);
            if (initParameter != null)
            {
                configPath = initParameter;
                logger.info("Setting config path from init parameter");
            }
            else
            {
                final String contextPath = context.getContextPath();
                configPath = "/srv/config" + contextPath;
                logger.info("Setting config path from context path");
            }
        }
        else
        {
            configPath = "./config";
            logger.info("Setting default config path");
        }

        Objects.requireNonNull(configPath);
        logger.info("Set config path to '{}'", configPath);
        return configPath;
    }

}
