import ClearIcon from "@mui/icons-material/Clear";
import SaveIcon from "@mui/icons-material/Save";
import Tooltip from "@mui/material/Tooltip";

import { Box, Stack } from "@mui/material";
import { useContext, useId } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../../context/ProjectContext";
import usePlotSelector from "../../hooks/usePlotSelector";
import { Details } from "../Details";

const ProjectPlotSelector = () => {
    const { t } = useTranslation();

    const { selectedProject } = useContext(ProjectContext);

    const id = useId();
    const { plotsChanged, handleCancelChange, handleSaveChange } = usePlotSelector(id);

    return (
        <Stack my={1} p={1} mb={10}>
            <Box sx={{ cursor: "pointer" }} position="absolute" right={10} top={55}>
                {plotsChanged && (
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
                <Stack overflow="auto" height="70vh">
                    <Details project={selectedProject} />
                </Stack>
                <div id={id} style={{ height: "70vh", width: "100%", paddingLeft: 8 }}></div>
            </Stack>
        </Stack>
    );
};

export default ProjectPlotSelector;
