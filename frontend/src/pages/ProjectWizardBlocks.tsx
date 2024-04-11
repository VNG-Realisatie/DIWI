import React, { useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { HouseBlock } from "../types/houseBlockTypes";
import { projectWizardMap, projectWizardWithId } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { HouseBlocksForm } from "../components/HouseBlocksForm";
import useAlert from "../hooks/useAlert";
import HouseBlockContext from "../context/HouseBlockContext";
import { useTranslation } from "react-i18next";
import { Accordion, AccordionDetails, AccordionSummary, IconButton, Stack } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import DeleteIcon from "@mui/icons-material/Delete";
import { deleteHouseBlock } from "../api/projectsServices";

const ProjectWizardBlocks = () => {
    const { houseBlocks, addHouseBlock, getEmptyHouseBlock, updateHouseBlock, refresh } = useContext(HouseBlockContext);
    const [houseBlocksState, setHouseBlocksState] = useState<HouseBlock[]>([getEmptyHouseBlock()]);
    const { projectId } = useParams();
    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const [canUpdate, setCanUpdate] = useState(true);
    const [expanded, setExpanded] = useState(Array.from({ length: houseBlocksState.length }, () => true));
    const [errorOccurred, setErrorOccurred] = useState(false);

    const { t } = useTranslation();

    useEffect(() => {
        if (houseBlocks.length > 0) {
            setExpanded(Array.from({ length: houseBlocks.length }, () => true));
        }
    }, [houseBlocks]);

    const handleNext = async () => {
        try {
            setErrorOccurred(false);

            await Promise.all(
                houseBlocksState.map(async (houseBlock) => {
                    if (
                        !houseBlock.houseblockName ||
                        !houseBlock.startDate ||
                        !houseBlock.endDate ||
                        houseBlock.ownershipValue.some((owner) => owner.amount === null || isNaN(owner.amount))
                    ) {
                        setErrorOccurred(true);
                        throw new Error(t("createProject.hasMissingRequiredAreas.hasmissingProperty"));
                    } else {
                        if (houseBlock.houseblockId) {
                            return updateHouseBlock(houseBlock);
                        } else {
                            return addHouseBlock(houseBlock);
                        }
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
        setCanUpdate(false);
        await Promise.all(
            houseBlocksState.map((houseBlock) => {
                if (houseBlock.houseblockId) {
                    return updateHouseBlock(houseBlock);
                } else {
                    return addHouseBlock(houseBlock);
                }
            }),
        );
        setCanUpdate(true);
        refresh();
    };

    const handleBack = () => {
        if (projectId) {
            navigate(projectWizardWithId.toPath({ projectId }));
        }
    };

    const handleAddHouseBlock = () => {
        const newHouseBlock = getEmptyHouseBlock();
        setHouseBlocksState([...houseBlocksState, newHouseBlock]);
        setExpanded([...expanded, true]);
    };

    useEffect(() => {
        if (houseBlocks.length > 0 && canUpdate) {
            const filteredHouseBlocksState = houseBlocksState.filter(
                (stateBlock) => !houseBlocks.some((block) => block.houseblockName === stateBlock.houseblockName),
            );
            const updatedHouseBlocksState = [...houseBlocks, ...filteredHouseBlocksState];

            setHouseBlocksState(updatedHouseBlocksState);
        }
    }, [houseBlocks, houseBlocksState, setHouseBlocksState, canUpdate]);

    const infoText = t("createProject.houseBlocksForm.info");

    const handleAccordionChange = (index: number) => {
        setExpanded((prevExpanded) => {
            const newExpanded = [...prevExpanded];
            newExpanded[index] = !newExpanded[index];
            return newExpanded;
        });
    };

    const handleDeleteHouseBlock = async (index: number, houseblockId?: string) => {
        try {
            if (houseblockId) {
                await deleteHouseBlock(houseblockId);
                setAlert("deleted successfully", "success");
                setHouseBlocksState((prevHouseBlocks) => prevHouseBlocks.filter((block) => block.houseblockId !== houseblockId));
            } else {
                setHouseBlocksState((prevHouseBlocks) => prevHouseBlocks.filter((_, i) => i !== index));
            }
        } catch (error) {
            console.error("Error deleting house block:", error);
            setAlert(t("errors.deleteHouseBlockError"), "error");
        }
    };

    return (
        <WizardLayout {...{ infoText, handleBack, handleNext, handleSave, projectId, activeStep: 1 }}>
            {houseBlocksState.map((houseBlock, index) => (
                <Accordion expanded={expanded[index]} onChange={() => handleAccordionChange(index)} sx={{ width: "100%" }} key={index}>
                    <AccordionSummary
                        sx={{ backgroundColor: "#00A9F3", color: "#ffffff" }}
                        expandIcon={<ExpandMoreIcon sx={{ color: "#ffffff" }} />}
                        aria-controls="panel1-content"
                        id="panel1-header"
                    >
                        {houseBlock.houseblockName
                            ? `${houseBlock.houseblockName}: ${houseBlock.mutation.grossPlanCapacity} ${t("createProject.houseBlocksForm.housesOn")} ${houseBlock.endDate}`
                            : `${t("generic.houseblock")} ${index + 1}`}
                        {houseBlocksState.length > 1 && (
                            <IconButton
                                sx={{ marginLeft: "auto" }}
                                onClick={(event) => {
                                    event.stopPropagation();
                                    handleDeleteHouseBlock(index, houseBlock.houseblockId);
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
            ))}
            <Stack direction="row" alignItems="center" mt={1} sx={{ cursor: "pointer" }} onClick={handleAddHouseBlock}>
                <AddCircleIcon color="info" sx={{ fontSize: "40px" }} />
                {t("createProject.addAnotherHouseBlock")}
            </Stack>
        </WizardLayout>
    );
};

export default ProjectWizardBlocks;
