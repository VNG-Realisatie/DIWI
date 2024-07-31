import ClearIcon from "@mui/icons-material/Clear";
import SaveIcon from "@mui/icons-material/Save";
import "mingcute_icon/font/Mingcute.css";
import Tooltip from "@mui/material/Tooltip";
import Button from "@mui/material/Button";

import { Box, Stack } from "@mui/material";
import { useContext, useId, useState } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../../context/ProjectContext";
import usePlotSelector, { Buttons } from "../../hooks/usePlotSelector";
import { Details } from "../Details";
import { AddHouseBlockButton } from "../PlusButton";
import { CreateHouseBlockDialog } from "../project/project-with-house-block/CreateHouseBlockDialog";

const ProjectPlotSelector = () => {
    const { t } = useTranslation();

    const { selectedProject } = useContext(ProjectContext);
    const [openHouseBlockDialog, setOpenHouseBlockDialog] = useState(false);
    const id = useId();
    const { plotsChanged, handleCancelChange, handleSaveChange, selectionMode, toggleSelectionMode } = usePlotSelector(id);

    const handleAddHouseBlockClick = () => {
        setOpenHouseBlockDialog(true);
    };

    return (
        <Stack my={1} p={1} mb={10}>
            <Box sx={{ cursor: "pointer" }} position="absolute" right={105} top={17}>
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
                <Stack overflow="auto" height="70vh" width="20%">
                    <Details project={selectedProject} />
                </Stack>
                <div id={id} style={{ height: "70vh", width: "100%", paddingLeft: 8 }}></div>
                <div style={{ position: "absolute", bottom: 100, right: 20 }}>
                    <AddHouseBlockButton onClick={handleAddHouseBlockClick} />
                    <CreateHouseBlockDialog openHouseBlockDialog={openHouseBlockDialog} setOpenHouseBlockDialog={setOpenHouseBlockDialog} />
                </div>
            </Stack>
            <Box position="absolute" top={90} right={20}>
                <Tooltip title={selectionMode ? t("generic.cancelSelection") : t("generic.selectPlot")}>
                    <Button
                        variant="contained"
                        onClick={() => toggleSelectionMode(Buttons.SELECT)}
                        sx={{
                            padding: 0,
                            backgroundColor: "#FFFFFF",
                            color: "#000000",
                            minWidth: "50px",
                            minHeight: "50px",
                            borderRadius: "10%",
                            "&:hover": {
                                backgroundColor: "#f0f0f0",
                            },
                        }}
                    >
                        <span
                            className={selectionMode === Buttons.SELECT ? "mgc_cursor_2_fill" : "mgc_cursor_2_line"}
                            style={{
                                fontSize: 24,
                            }}
                        ></span>
                    </Button>
                </Tooltip>
                <Tooltip title={selectionMode ? t("generic.cancelCutSelection") : t("generic.cutSelection")}>
                    <Button
                        variant="contained"
                        onClick={() => toggleSelectionMode(Buttons.CUT)}
                        sx={{
                            padding: 0,
                            backgroundColor: "#FFFFFF",
                            color: "#000000",
                            minWidth: "50px",
                            minHeight: "50px",
                            borderRadius: "10%",
                            "&:hover": {
                                backgroundColor: "#f0f0f0",
                            },
                        }}
                    >
                        <span
                            className={selectionMode === Buttons.CUT ? "mgc_scissors_2_fill" : "mgc_scissors_2_line"}
                            style={{
                                fontSize: 24,
                            }}
                        ></span>
                    </Button>
                </Tooltip>
            </Box>
        </Stack>
    );
};

export default ProjectPlotSelector;
