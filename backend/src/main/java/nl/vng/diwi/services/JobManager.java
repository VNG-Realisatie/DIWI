package nl.vng.diwi.services;

import nl.vng.diwi.config.ProjectConfig;
import nl.vng.diwi.dal.DalFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.util.Date;

public class JobManager {

    private static final Logger logger = LogManager.getLogger();

    public static final String REPO_FACTORY_STR = "repoFactory";
    public static final String INVOKE_PYTHON_STR = "pythonService";

    private DalFactory dalFactory;
    private Scheduler scheduler;

    public JobManager(DalFactory dalFactory, ProjectConfig projectConfig, Scheduler scheduler) {
        logger.info("Start RestTaskManager");

        this.dalFactory = dalFactory;
        this.scheduler = scheduler;
    }

    public boolean startJobNow(Class<? extends Job> jobClass, String jobKey, JobDataMap jobDataMap) {
        if (isJobExists(jobKey)) {
            return false;
        }

        jobDataMap.put(REPO_FACTORY_STR, dalFactory);

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
            .withIdentity(jobKey)
            .usingJobData(jobDataMap)
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("Trigger_" + jobKey)
            .forJob(jobDetail)
            .startAt(new Date((new Date()).getTime() + 3000))
            .build();

        try {
            if (scheduler != null) {
                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    private boolean isJobExists(String keyStr) {
        JobKey jobKey = new JobKey(keyStr);
        try {
            return scheduler.checkExists(jobKey);
        } catch (SchedulerException e) {
            return false;
        }
    }

}
