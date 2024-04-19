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

export type DateValidationErrors = {
    startDateError: string | null;
    endDateError: string | null;
};

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
    const [dateValidationErrors, setDateValidationErrors] = useState<Array<DateValidationErrors>>([]);

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
        let hasErrors = false;
        const newDateValidationErrors: DateValidationErrors = { startDateError: null, endDateError: null };

        if (!houseBlock.startDate) {
            newDateValidationErrors.startDateError = t("wizard.houseBlocks.startDateWarningMissing");
            hasErrors = true;
        }
        if (!houseBlock.endDate) {
            newDateValidationErrors.endDateError = t("wizard.houseBlocks.endDateWarningMissing");
            hasErrors = true;
        }

        if (!houseBlock.houseblockName || houseBlock.ownershipValue.some((owner: any) => owner.amount === null || isNaN(owner.amount))) {
            hasErrors = true;
        }
        if (houseBlock.startDate && houseBlock.endDate && selectedProject?.startDate && selectedProject?.endDate) {
            const houseBlockStartDate = new Date(houseBlock.startDate);
            const houseBlockEndDate = new Date(houseBlock.endDate);
            const selectedProjectStartDate = new Date(selectedProject.startDate);
            const selectedProjectEndDate = new Date(selectedProject.endDate);

            if (houseBlockStartDate < selectedProjectStartDate) {
                newDateValidationErrors.startDateError = t("wizard.houseBlocks.startDateWarning");
                hasErrors = true;
            }
            if (houseBlockEndDate > selectedProjectEndDate) {
                newDateValidationErrors.endDateError = t("wizard.houseBlocks.endDateWarning");
                hasErrors = true;
            }
        }

        setDateValidationErrors((prevDateValidationErrors) => {
            const newDateValidationErrorsArray = [...prevDateValidationErrors];
            newDateValidationErrorsArray[index] = newDateValidationErrors;
            return newDateValidationErrorsArray;
        });

        if (hasErrors) {
            setErrors((prevErrors) => {
                const newErrorsArray = [...prevErrors];
                newErrorsArray[index] = true;
                return newErrorsArray;
            });
            throw new Error(t("createProject.houseBlocksForm.notifications.error"));
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
            setDateValidationErrors((prevErrors) => {
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
                                    ? `${houseBlock.houseblockName}: ${houseBlock.mutation.amount} ${t("createProject.houseBlocksForm.housesOn")} ${houseBlock.endDate}`
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
                                    errors={dateValidationErrors[index]}
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
