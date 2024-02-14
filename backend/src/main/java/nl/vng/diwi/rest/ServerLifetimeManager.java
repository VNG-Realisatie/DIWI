package nl.vng.diwi.rest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLifetimeManager implements ServletContextListener
{
    private static final Logger logger = LogManager.getLogger(ServerLifetimeManager.class.getSimpleName());

    @Override
    public void contextInitialized(ServletContextEvent contextEvent)
    {
        final ServletContext context = contextEvent.getServletContext();
        logger.info("Vng starting up at {}", context.getContextPath());
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent)
    {
        logger.info("Vng stopping at {}", contextEvent.getServletContext().getContextPath());
    }
}
