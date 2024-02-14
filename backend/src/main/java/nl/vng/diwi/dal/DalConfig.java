package nl.vng.diwi.dal;

import java.util.UUID;

public class DalConfig
{
    public String ormConfigFile = "src/test/resources/hibernate-h2.cfg.xml";
    public String dbName;

    public DalConfig()
    {

    }

    public DalConfig(String ormConfigFile)
    {
        this.ormConfigFile = ormConfigFile;
    }

    public DalConfig(String ormConfigFilename, String dbName)
    {
        this.ormConfigFile = ormConfigFilename;
        this.dbName = dbName;
    }

    public String randomizeDbName(String dbNamePrefix)
    {
        String randomName = createRandomDbName(dbNamePrefix);
        dbName = randomName;
        return randomName;
    }

    public static String createRandomDbName(String dbNamePrefix)
    {
        String randomName = dbNamePrefix + UUID.randomUUID().toString();
        return randomName;
    }
}
