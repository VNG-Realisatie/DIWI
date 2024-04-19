import React, { useContext, useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { HouseBlockWithCustomProperties } from "../types/houseBlockTypes";
import { projectWizardMap, projectWizardWithId } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { HouseBlocksForm } from "../components/HouseBlocksForm";
import useAlert from "../hooks/useAlert";
import HouseBlockContext from "../context/HouseBlockContext";
import { useTranslation } from "react-i18next";
import { Accordion, AccordionDetails, AccordionSummary, Box, Stack } from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { deleteHouseBlockWithCustomProperties } from "../api/houseBlockServices";
import ProjectContext from "../context/ProjectContext";
import { DeleteButtonWithConfirm } from "../components/DeleteButtonWithConfirm";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import { saveHouseBlockWithCustomProperties } from "../api/houseBlockServices";
import useLoading from "../hooks/useLoading";

const generateTemporaryId = () => Date.now();

const ProjectWizardBlocks = () => {
    const { houseBlocks, getEmptyHouseBlock, refresh } = useContext(HouseBlockContext);
    const { selectedProject } = useContext(ProjectContext);
    const [houseBlocksState, setHouseBlocksState] = useState<HouseBlockWithCustomProperties[]>([{ ...getEmptyHouseBlock(), tempId: generateTemporaryId() }]);
    const { projectId } = useParams();
    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const [expanded, setExpanded] = useState(Array.from({ length: houseBlocksState.length }, () => true));
    const [errorOccurred, setErrorOccurred] = useState(false);
    const [errors, setErrors] = useState<boolean[]>(Array.from({ length: houseBlocksState.length }, () => false));
    const lastAddedForm = useRef<HTMLDivElement | null>(null);
    const { setLoading } = useLoading();

    async function saveHouseBlock(houseBlock: HouseBlockWithCustomProperties) {
        try {
            setLoading(true);
            const res = await saveHouseBlockWithCustomProperties(houseBlock);
            setAlert(t("createProject.houseBlocksForm.notifications.successfullySaved"), "success");
            return res.houseblockId;
        } catch {
            setAlert(t("createProject.houseBlocksForm.notifications.error"), "error");
        } finally {
            setLoading(false);
        }
    }

    const { t } = useTranslation();

    const validateHouseBlock = (houseBlock: HouseBlockWithCustomProperties, selectedProject: any, index: number) => {
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
        const isSuccessful: boolean | undefined = await handleSave();
        if (!isSuccessful) return;

        if (projectId) navigate(projectWizardMap.toPath({ projectId }));
    };

    const handleSave = async () => {
        setErrors(Array.from({ length: houseBlocksState.length }, () => false));
        try {
            setErrorOccurred(false);

            const res = await Promise.all(
                houseBlocksState.map(async (houseBlock, index) => {
                    try {
                        validateHouseBlock(houseBlock, selectedProject, index);
                        const id = await saveHouseBlock({ ...houseBlock, tempId: undefined });
                        return { id, tempId: houseBlock.tempId, houseBlockId: houseBlock.houseblockId };
                    } catch (error) {
                        setErrorOccurred(true);
                        return { error };
                    }
                }),
            );

            const hasErrors = res.some((response) => response.error);

            const updatedHouseBlocks = houseBlocksState.map((houseBlockState) => {
                const result = res.find((response) => response.tempId === houseBlockState.tempId);
                if (result && result.id && !result.error) {
                    if (houseBlockState.houseblockId) {
                        return houseBlockState;
                    }
                    return { ...houseBlockState, houseblockId: result.id };
                }
                return houseBlockState;
            });

            setHouseBlocksState(updatedHouseBlocks);
            return !hasErrors;
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
        const newHouseBlock = { ...getEmptyHouseBlock(), tempId: generateTemporaryId() };
        const newExpanded = Array.from({ length: houseBlocksState.length }, () => false);
        newExpanded.push(true);

        setHouseBlocksState([...houseBlocksState, newHouseBlock]);
        setExpanded(newExpanded);
        if (lastAddedForm.current) {
            lastAddedForm.current.scrollIntoView({ behavior: "smooth", block: "start" });
        }
    };

    useEffect(() => {
        if (houseBlocks.length >= houseBlocksState.length) {
            setHouseBlocksState(houseBlocks);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [houseBlocks, setHouseBlocksState]);

    useEffect(() => {
        if (errors.every((element) => element === false)) {
            setErrorOccurred(false);
        }
    }, [errors]);

    const handleAccordionChange = (index: number) => {
        setExpanded((prevExpanded) => {
            const newExpanded = [...prevExpanded];
            newExpanded[index] = !newExpanded[index];
            return newExpanded;
        });
    };

    const handleDeleteHouseBlock = async (index: number, houseblockId?: string, name?: string, tempId?: number) => {
        try {
            if (houseblockId) {
                await deleteHouseBlockWithCustomProperties(houseblockId);
                setAlert(t("generic.deletionSuccess", { name }), "success");
                setHouseBlocksState((prevHouseBlocks) => prevHouseBlocks.filter((houseBlock) => houseBlock.houseblockId !== houseblockId));
            } else {
                setHouseBlocksState((prevHouseBlocks) => prevHouseBlocks.filter((houseBlock) => houseBlock.tempId !== tempId));
            }
            setErrors((prevErrors) => {
                const newErrors = [...prevErrors];
                newErrors.splice(index, 1);
                return newErrors;
            });
        } catch (error: any) {
            setAlert(error.message, "warning");
        }
    };

    const infoText = t("createProject.houseBlocksForm.info");
    const warning = errorOccurred ? t("wizard.houseBlocks.warning") : undefined;
    return (
        <WizardLayout {...{ infoText, warning, handleBack, handleNext, handleSave, projectId, activeStep: 1 }}>
            {houseBlocksState.map((houseBlock, index) => (
                <React.Fragment key={houseBlock.houseblockId ?? houseBlock.tempId}>
                    <Box ref={index === houseBlocksState.length - 1 ? lastAddedForm : null}>
                        <Accordion expanded={expanded[index]} onChange={() => handleAccordionChange(index)} sx={{ width: "100%" }}>
                            <AccordionSummary
                                sx={{ backgroundColor: "#00A9F3", color: "#ffffff" }}
                                expandIcon={<ExpandMoreIcon sx={{ color: "#ffffff" }} />}
                                aria-controls={`panel${index + 1}-content`}
                                id={`panel${index + 1}-header`}
                            >
                                {errors[index] === true ? <ErrorOutlineIcon sx={{ marginRight: 1, color: "#ff9800" }} /> : null}
                                {houseBlock.houseblockId
                                    ? `${houseBlock.houseblockName}: ${houseBlock.mutation.grossPlanCapacity} ${t("createProject.houseBlocksForm.housesOn")} ${houseBlock.endDate}`
                                    : `${t("generic.houseblock")} ${index + 1}`}
                                {houseBlocksState.length > 1 && (
                                    <Box sx={{ marginLeft: "auto", marginTop: "5px", marginRight: "5px" }}>
                                        <DeleteButtonWithConfirm
                                            typeAndName={`${t("generic.houseblock")} ${houseBlock.houseblockName}`}
                                            iconColor={"white"}
                                            deleteFunction={() =>
                                                handleDeleteHouseBlock(index, houseBlock.houseblockId, houseBlock.houseblockName, houseBlock.tempId)
                                            }
                                            afterDelete={refresh}
                                        />
                                    </Box>
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
                    </Box>
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
