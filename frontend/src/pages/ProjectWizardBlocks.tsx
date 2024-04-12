import React, { useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { HouseBlock } from "../types/houseBlockTypes";
import { projectWizardMap, projectWizardWithId } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { HouseBlocksForm } from "../components/HouseBlocksForm";
import useAlert from "../hooks/useAlert";
import HouseBlockContext from "../context/HouseBlockContext";
import { useTranslation } from "react-i18next";
import { Accordion, AccordionDetails, AccordionSummary, Box, Button, Dialog, DialogActions, DialogTitle, IconButton, Stack } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import DeleteIcon from "@mui/icons-material/Delete";
import { deleteHouseBlock } from "../api/projectsServices";
import ProjectContext from "../context/ProjectContext";

const ProjectWizardBlocks = () => {
    const { houseBlocks, addHouseBlock, getEmptyHouseBlock, updateHouseBlock, refresh } = useContext(HouseBlockContext);
    const { selectedProject } = useContext(ProjectContext);
    const [houseBlocksState, setHouseBlocksState] = useState<HouseBlock[]>([getEmptyHouseBlock()]);
    const { projectId } = useParams();
    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const [canUpdate, setCanUpdate] = useState(true);
    const [expanded, setExpanded] = useState(Array.from({ length: houseBlocksState.length }, () => true));
    const [errorOccurred, setErrorOccurred] = useState(false);
    const [isDialogOpen, setIsDialogOpen] = useState<boolean>(false);
    const [errors, setErrors] = useState<boolean[]>(Array.from({ length: houseBlocksState.length }, () => false));

    const { t } = useTranslation();

    const validateHouseBlock = (houseBlock: any, selectedProject: any, index: number) => {
        if (
            !houseBlock.houseblockName ||
            !houseBlock.startDate ||
            !houseBlock.endDate ||
            houseBlock.ownershipValue.some((owner: any) => owner.amount === null || isNaN(owner.amount))
        ) {
            setErrors((prevErrors) => {
                const newErrors = [...prevErrors];
                newErrors[index] = true;
                return newErrors;
            });
            throw new Error(t("createProject.hasMissingRequiredAreas.hasmissingProperty"));
        } else if (selectedProject?.startDate && selectedProject?.endDate) {
            const houseBlockStartDate = new Date(houseBlock.startDate);
            const houseBlockEndDate = new Date(houseBlock.endDate);
            const selectedProjectStartDate = new Date(selectedProject.startDate);
            const selectedProjectEndDate = new Date(selectedProject.endDate);

            if (houseBlockStartDate < selectedProjectStartDate) {
                throw new Error(t("wizard.houseBlocks.startDateWarning"));
            } else if (houseBlockEndDate > selectedProjectEndDate) {
                throw new Error(t("wizard.houseBlocks.endDateWarning"));
            }
        }
    };

    const handleNext = async () => {
        setErrors(Array.from({ length: houseBlocksState.length }, () => false));
        try {
            setErrorOccurred(false);

            await Promise.all(
                houseBlocksState.map(async (houseBlock, index) => {
                    try {
                        validateHouseBlock(houseBlock, selectedProject, index);
                        if (houseBlock.houseblockId) {
                            return updateHouseBlock(houseBlock);
                        } else {
                            return addHouseBlock(houseBlock);
                        }
                    } catch (error) {
                        setErrorOccurred(true);
                        throw error;
                    }
                }),
            );

            if (!errorOccurred && projectId) {
                navigate(projectWizardMap.toPath({ projectId }));
            }
        } catch (error: any) {
            setErrorOccurred(true);
            setAlert(error.message, "warning");
        }
    };

    const handleSave = async () => {
        setErrors(Array.from({ length: houseBlocksState.length }, () => false));
        try {
            setErrorOccurred(false);

            await Promise.all(
                houseBlocksState.map(async (houseBlock, index) => {
                    try {
                        validateHouseBlock(houseBlock, selectedProject, index);
                        if (houseBlock.houseblockId) {
                            return updateHouseBlock(houseBlock);
                        } else {
                            return addHouseBlock(houseBlock);
                        }
                    } catch (error) {
                        setErrorOccurred(true);
                        throw error;
                    }
                }),
            );

            setCanUpdate(true);
            refresh();
        } catch (error: any) {
            setErrorOccurred(true);
            setAlert(error.message, "warning");
        }
    };

    const handleBack = () => {
        if (projectId) {
            navigate(projectWizardWithId.toPath({ projectId }));
        }
    };

    const handleAddHouseBlock = () => {
        const newHouseBlock = getEmptyHouseBlock();
        const newExpanded = Array.from({ length: houseBlocksState.length }, () => false);
        newExpanded.push(true);

        setHouseBlocksState([...houseBlocksState, newHouseBlock]);
        setExpanded(newExpanded);
    };

    useEffect(() => {
        if (houseBlocks.length > 0 && canUpdate) {
            const updatedHouseBlocksState = houseBlocksState.map((houseBlockState) => {
                const matchingHouseBlock = houseBlocks.find((houseBlock) => houseBlock.houseblockName === houseBlockState.houseblockName);
                if (matchingHouseBlock) {
                    return {
                        ...houseBlockState,
                        houseblockId: matchingHouseBlock.houseblockId,
                    };
                } else {
                    return houseBlockState;
                }
            });
            setHouseBlocksState(updatedHouseBlocksState);
        }
        if (houseBlocks.length >= houseBlocksState.length) {
            setHouseBlocksState(houseBlocks);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [houseBlocks, setHouseBlocksState, canUpdate]);

    const handleAccordionChange = (index: number) => {
        setExpanded((prevExpanded) => {
            const newExpanded = [...prevExpanded];
            newExpanded[index] = !newExpanded[index];
            return newExpanded;
        });
    };

    const handleDeleteHouseBlock = async (index: number, houseblockId?: string, name?: string) => {
        try {
            if (houseblockId) {
                await deleteHouseBlock(houseblockId);
                setAlert(t("generic.deletionSuccess", { name }), "success");
                setHouseBlocksState((prevHouseBlocks) => prevHouseBlocks.filter((houseBlock) => houseBlock.houseblockId !== houseblockId));
            } else {
                setHouseBlocksState((prevHouseBlocks) => prevHouseBlocks.filter((_, i) => i !== index));
            }
        } catch (error: any) {
            setAlert(error.message, "warning");
        } finally {
            setIsDialogOpen(false);
        }
    };

    const infoText = t("createProject.houseBlocksForm.info");
    const warning = errorOccurred ? t("wizard.houseBlocks.warning") : undefined;

    console.log(errors);

    return (
        <WizardLayout {...{ infoText, warning, handleBack, handleNext, handleSave, projectId, activeStep: 1 }}>
            {houseBlocksState.map((houseBlock, index) => (
                <React.Fragment key={index}>
                    <Accordion expanded={expanded[index]} onChange={() => handleAccordionChange(index)} sx={{ width: "100%" }}>
                        <AccordionSummary
                            sx={{ backgroundColor: "#00A9F3", color: "#ffffff" }}
                            expandIcon={<ExpandMoreIcon sx={{ color: "#ffffff" }} />}
                            aria-controls={`panel${index + 1}-content`}
                            id={`panel${index + 1}-header`}
                        >
                            {errors[index] === true ? "ERROR" : ""}
                            {houseBlock.houseblockId
                                ? `${houseBlock.houseblockName}: ${houseBlock.mutation.grossPlanCapacity} ${t("createProject.houseBlocksForm.housesOn")} ${houseBlock.endDate}`
                                : `${t("generic.houseblock")} ${index + 1}`}
                            {houseBlocksState.length > 1 && (
                                <IconButton
                                    sx={{ marginLeft: "auto" }}
                                    onClick={(event) => {
                                        event.stopPropagation();
                                        setIsDialogOpen(true);
                                    }}
                                >
                                    <DeleteIcon sx={{ color: "#ffffff" }} />
                                </IconButton>
                            )}
                        </AccordionSummary>
                        <AccordionDetails>
                            <HouseBlocksForm
                                readOnly={false}
                                houseBlock={houseBlock}
                                setHouseBlock={(updatedHouseBlock) => {
                                    const updatedBlocks = [...houseBlocksState];
                                    updatedBlocks[index] = updatedHouseBlock;
                                    setHouseBlocksState(updatedBlocks);
                                }}
                            />
                        </AccordionDetails>
                    </Accordion>
                    {isDialogOpen && (
                        <Dialog open={isDialogOpen} onClose={() => setIsDialogOpen(false)}>
                            <DialogTitle>{t("wizard.houseBlocks.deleteConfirmation")}</DialogTitle>
                            <DialogActions>
                                <Box sx={{ display: "flex", gap: "10px" }}>
                                    <Button onClick={() => setIsDialogOpen(false)} variant="outlined">
                                        {t("generic.no")}
                                    </Button>
                                    <Button
                                        onClick={() => handleDeleteHouseBlock(index, houseBlock.houseblockId, houseBlock.houseblockName)}
                                        variant="contained"
                                    >
                                        {t("generic.yes")}
                                    </Button>
                                </Box>
                            </DialogActions>
                        </Dialog>
                    )}
                </React.Fragment>
            ))}
            <Stack direction="row" alignItems="center" mt={1} sx={{ cursor: "pointer" }} onClick={handleAddHouseBlock}>
                <AddCircleIcon color="info" sx={{ fontSize: "40px" }} />
                {t("createProject.addAnotherHouseBlock")}
            </Stack>
        </WizardLayout>
    );
};

export default ProjectWizardBlocks;
