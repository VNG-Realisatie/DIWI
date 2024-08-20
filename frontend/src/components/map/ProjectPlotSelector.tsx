import ClearIcon from "@mui/icons-material/Clear";
import SaveIcon from "@mui/icons-material/Save";
import Tooltip from "@mui/material/Tooltip";

import { Box, Stack } from "@mui/material";
import { useContext, useId, useState } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../../context/ProjectContext";
import usePlotSelector from "../../hooks/usePlotSelector";
import { Details } from "../Details";
import { AddHouseBlockButton } from "../PlusButton";
import { CreateHouseBlockDialog } from "../project/project-with-house-block/CreateHouseBlockDialog";
import { useHasEditPermission } from "../../hooks/useHasEditPermission";

const ProjectPlotSelector = () => {
    const { t } = useTranslation();

    const { selectedProject } = useContext(ProjectContext);
    const [openHouseBlockDialog, setOpenHouseBlockDialog] = useState(false);
    const id = useId();
    const { plotsChanged, handleCancelChange, handleSaveChange } = usePlotSelector(id);
    const { getEditPermission } = useHasEditPermission();

    const handleAddHouseBlockClick = () => {
        setOpenHouseBlockDialog(true);
    };

    return (
        <Stack my={1} p={1} mb={10}>
            <Box sx={{ cursor: "pointer" }} position="absolute" right={105} top={17}>
                {plotsChanged && getEditPermission() && (
                    <>
                        <Tooltip placement="top" title={t("generic.cancelChanges")}>
                            <ClearIcon sx={{ mr: 2, color: "#FFFFFF" }} onClick={handleCancelChange} />
                        </Tooltip>
                        <Tooltip placement="top" title={t("generic.saveChanges")}>
                            <SaveIcon sx={{ color: "#FFFFFF" }} onClick={handleSaveChange} />
                        </Tooltip>
                    </>
                )}
            </Box>
            <Stack direction="row" alignItems="center" justifyContent="space-between">
                <Stack overflow="auto" height="70vh" width="20%">
                    <Details project={selectedProject} />
                </Stack>
                <div id={id} style={{ height: "70vh", width: "100%", paddingLeft: 8 }}></div>
                <div style={{ position: "absolute", bottom: 100, right: 20 }}>
                    <AddHouseBlockButton onClick={handleAddHouseBlockClick} />
                    <CreateHouseBlockDialog openHouseBlockDialog={openHouseBlockDialog} setOpenHouseBlockDialog={setOpenHouseBlockDialog} />
                </div>
            </Stack>
        </Stack>
    );
};

export default ProjectPlotSelector;
