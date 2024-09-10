import ClearIcon from "@mui/icons-material/Clear";
import SaveIcon from "@mui/icons-material/Save";
import Tooltip from "@mui/material/Tooltip";
import Button from "@mui/material/Button";
import MouseIcon from "@mui/icons-material/Mouse";
import ContentCutIcon from "@mui/icons-material/ContentCut";
import DeleteIcon from "@mui/icons-material/Delete";
import { Box, Stack } from "@mui/material";
import { useId, useState } from "react";
import { useTranslation } from "react-i18next";
import usePlotSelector, { Buttons } from "../../hooks/usePlotSelector";
import { Details } from "../Details";
import { AddHouseBlockButton } from "../PlusButton";
import { CreateHouseBlockDialog } from "../project/project-with-house-block/CreateHouseBlockDialog";
import { useHasEditPermission } from "../../hooks/useHasEditPermission";

const ProjectPlotSelector = () => {
    const { t } = useTranslation();

    const [openHouseBlockDialog, setOpenHouseBlockDialog] = useState(false);
    const id = useId();
    const { plotsChanged, handleCancelChange, handleSaveChange, selectionMode, toggleSelectionMode } = usePlotSelector(id);
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
                    <Details />
                </Stack>
                <div id={id} style={{ height: "70vh", width: "100%", paddingLeft: 8 }}></div>
                <div style={{ position: "absolute", bottom: 100, right: 20 }}>
                    <AddHouseBlockButton onClick={handleAddHouseBlockClick} />
                    <CreateHouseBlockDialog openHouseBlockDialog={openHouseBlockDialog} setOpenHouseBlockDialog={setOpenHouseBlockDialog} />
                </div>
            </Stack>
            <Box position="absolute" top={90} right={20}>
                {getEditPermission() && (
                    <>
                        <Tooltip title={selectionMode === Buttons.SELECT ? t("generic.cancelSelection") : t("generic.selectPlot")}>
                            <Button
                                variant="contained"
                                onClick={() => toggleSelectionMode(Buttons.SELECT)}
                                sx={{
                                    padding: 0,
                                    backgroundColor: "#FFFFFF",
                                    color: selectionMode === Buttons.SELECT ? "blue" : "black",
                                    minWidth: "50px",
                                    minHeight: "50px",
                                    borderRadius: "10%",
                                    "&:hover": {
                                        backgroundColor: "#f0f0f0",
                                    },
                                }}
                            >
                                <MouseIcon />
                            </Button>
                        </Tooltip>
                        <Tooltip title={selectionMode === Buttons.CUT ? t("generic.cancelCutSelection") : t("generic.cutSelection")}>
                            <Button
                                variant="contained"
                                onClick={() => toggleSelectionMode(Buttons.CUT)}
                                sx={{
                                    padding: 0,
                                    marginLeft: "10px",
                                    backgroundColor: "#FFFFFF",
                                    color: selectionMode === Buttons.CUT ? "orangered" : "black",
                                    minWidth: "50px",
                                    minHeight: "50px",
                                    borderRadius: "10%",
                                    "&:hover": {
                                        backgroundColor: "#f0f0f0",
                                    },
                                }}
                            >
                                <ContentCutIcon />
                            </Button>
                        </Tooltip>
                        <Tooltip title={selectionMode === Buttons.DELETE ? t("generic.cancelDeleteSelection") : t("generic.deleteSelection")}>
                            <Button
                                variant="contained"
                                onClick={() => toggleSelectionMode(Buttons.DELETE)}
                                sx={{
                                    padding: 0,
                                    marginLeft: "10px",
                                    backgroundColor: "#FFFFFF",
                                    color: selectionMode === Buttons.DELETE ? "red" : "black",
                                    minWidth: "50px",
                                    minHeight: "50px",
                                    borderRadius: "10%",
                                    "&:hover": {
                                        backgroundColor: "#f0f0f0",
                                    },
                                }}
                            >
                                <DeleteIcon />
                            </Button>
                        </Tooltip>
                    </>
                )}
            </Box>
        </Stack>
    );
};

export default ProjectPlotSelector;
