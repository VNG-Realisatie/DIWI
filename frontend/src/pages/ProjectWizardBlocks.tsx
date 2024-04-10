import React, { useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { HouseBlock } from "../components/project-wizard/house-blocks/types";
import { projectWizardMap, projectWizardWithId } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { HouseBlocksForm } from "../components/HouseBlocksForm";
import useAlert from "../hooks/useAlert";
import HouseBlockContext from "../context/HouseBlockContext";
import { t } from "i18next";
import { Accordion, AccordionDetails, AccordionSummary, IconButton, Stack } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import DeleteIcon from "@mui/icons-material/Delete";

const ProjectWizardBlocks = () => {
    const { houseBlocks, addHouseBlock, getEmptyHouseBlock, updateHouseBlock, refresh } = useContext(HouseBlockContext);
    const [houseBlocksState, setHouseBlocksState] = useState<HouseBlock[]>([getEmptyHouseBlock()]);
    const { projectId } = useParams();
    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const [canUpdate, setCanUpdate] = useState(true);
    const [expanded, setExpanded] = useState(Array.from({ length: houseBlocksState.length }, () => true));

    useEffect(() => {
        if (houseBlocks.length > 0) {
            setExpanded(Array.from({ length: houseBlocks.length }, () => true));
        }
    }, [houseBlocks]);

    const handleNext = async () => {
        houseBlocksState.forEach((houseBlock) => {
            if (
                !houseBlock.houseblockName ||
                !houseBlock.startDate ||
                !houseBlock.endDate ||
                houseBlock.ownershipValue.some((owner) => owner.amount === null || isNaN(owner.amount))
            ) {
                setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
            } else {
                if (houseBlock.houseblockId) {
                    updateHouseBlock(houseBlock);
                } else {
                    addHouseBlock(houseBlock);
                }
                if (projectId) {
                    navigate(projectWizardMap.toPath({ projectId }));
                }
            }
        });
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
        if (houseBlocks.length > 0 && canUpdate === true) {
            setHouseBlocksState(houseBlocks);
        }
    }, [houseBlocks, setHouseBlocksState, canUpdate]);

    const infoText = t("createProject.houseBlocksForm.info");

    const handleAccordionChange = (index: number) => {
        setExpanded((prevExpanded) => {
            const newExpanded = [...prevExpanded];
            newExpanded[index] = !newExpanded[index];
            return newExpanded;
        });
    };

    const handleDeleteHouseBlock = (index: number) => {
        // Implement delete logic here
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
                        <IconButton
                            sx={{ marginLeft: "auto" }}
                            onClick={(event) => {
                                event.stopPropagation();
                                handleDeleteHouseBlock(index);
                            }}
                        >
                            <DeleteIcon sx={{ color: "#ffffff" }} />
                        </IconButton>
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
