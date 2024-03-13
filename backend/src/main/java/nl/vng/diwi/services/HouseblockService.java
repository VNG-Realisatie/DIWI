package nl.vng.diwi.services;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.dal.entities.HouseblockSnapshotSqlModel;
import nl.vng.diwi.rest.VngNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

public class HouseblockService {
    private static final Logger logger = LogManager.getLogger();

    public HouseblockService() {
    }

    public HouseblockSnapshotModel getHouseblockSnapshot(VngRepository repo, UUID houseblockUuid) throws VngNotFoundException {

        HouseblockSnapshotSqlModel houseblockSnapshotModel = repo.getHouseblockDAO().getHouseblockByUuid(houseblockUuid);

        if (houseblockSnapshotModel == null) {
            logger.error("Houseblock with uuid {} was not found.", houseblockUuid);
            throw new VngNotFoundException();
        }
        return new HouseblockSnapshotModel(houseblockSnapshotModel);
    }

    public List<HouseblockSnapshotModel> getProjectHouseblocks(VngRepository repo, UUID projectUuid) {

        List<HouseblockSnapshotSqlModel> sqlHouseblocks = repo.getHouseblockDAO().getHouseblocksByProjectUuid(projectUuid);

        return sqlHouseblocks.stream().map(HouseblockSnapshotModel::new).toList();
    }
}
