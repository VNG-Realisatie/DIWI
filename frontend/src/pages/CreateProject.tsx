import React, { useEffect, useId, useState } from "react";
import { Stepper, Step, StepLabel, Button, Box, Stack } from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import { ProjectInformationForm } from "../components/project/ProjectInformationForm";
import { BlockHousesForm } from "../components/BlockHousesForm";
// import { SelectFromMapForm } from "../components/SelectFromMapForm";
// import { TimelineForm } from "../components/TimelineForm";
import useAlert from "../hooks/useAlert";
import { useTranslation } from "react-i18next";
import { updateProject, createProject, addHouseBlock, getProject } from "../api/projectsServices";
import { useParams, useNavigate } from "react-router-dom";
import { HouseBlock } from "../components/project-wizard/house-blocks/types";
import { emptyHouseBlockForm } from "../components/project-wizard/house-blocks/constants";
import dayjs from "dayjs";
import usePlotSelector from "../hooks/usePlotSelector";

const CustomStepIcon: React.FC<CustomStepIconProps> = ({ active, completed }) => {
    if (completed) {
        return <CheckCircleIcon color="primary" />;
    } else {
        return <RadioButtonUncheckedIcon color={active ? "primary" : "disabled"} />;
    }
};

const steps: string[] = [
    "information",
    "houseBlocks",
    "signupOnTheMap",
    // "timeline"
];

interface CustomStepIconProps {
    active: boolean;
    completed: boolean;
}
export const CreateProject = () => {
    const [createProjectForm, setCreateProjectForm] = useState<any>({ projectColor: "#FF5733" });
    const [createFormHouseBlock, setCreateFormHouseBlock] = useState<HouseBlock>(emptyHouseBlockForm);
    const [activeStep, setActiveStep] = useState<number>(0);
    const [validationError, setValidationError] = useState("");

    const { id: projectId } = useParams();
    const navigate = useNavigate();

    const { setAlert } = useAlert();

    const { t } = useTranslation();

    const handleSave = async () => {
        if (
            !createProjectForm.projectName ||
            !createProjectForm.startDate ||
            !createProjectForm.endDate ||
            !createProjectForm.projectColor ||
            !createProjectForm.projectPhase ||
            !createProjectForm.confidentialityLevel
        ) {
            setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
            return;
        }
        try {
            if (projectId) {
                setValidationError("");
                const res = await updateProject(projectId, createProjectForm);
                if (res.ok) {
                    setAlert(t("createProject.successfullySaved"), "success");
                    return true;
                }
            } else {
                const temporaryCreateForm = {
                    projectName: createProjectForm.projectName,
                    projectColor: createProjectForm.projectColor,
                    projectPhase: createProjectForm.projectPhase,
                    confidentialityLevel: createProjectForm.confidentialityLevel,
                    startDate: createProjectForm.startDate,
                    endDate: createProjectForm.endDate,
                };
                setValidationError("");
                const project = await createProject(temporaryCreateForm); //TODO later it will be change with createProjectForm

                navigate(`/project/create/${project.projectId}`);
                setAlert(t("createProject.successfullySaved"), "success");
            }
        } catch (error: any) {
            setAlert(error.message, "error");
            return false;
        }
    };

    const handleNext = async () => {
        if (activeStep === 1) {
            if (!createFormHouseBlock.houseblockName) {
                setValidationError("houseblockName");
                return;
            } else if (!createFormHouseBlock.startDate) {
                setValidationError("startDate");
                return;
            } else if (!createFormHouseBlock.endDate) {
                setValidationError("endDate");
                return;
            } else if (createFormHouseBlock.ownershipValue.some((owner) => owner.amount === null || isNaN(owner.amount))) {
                setValidationError("value");
                return;
            }
            await addHouseBlock({ ...createFormHouseBlock, projectId });
            setActiveStep((prevActiveStep) => prevActiveStep + 1);
            return;
        }
        setActiveStep((prevActiveStep) => prevActiveStep + 1);
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    useEffect(() => {
        if (projectId) {
            getProject(projectId).then((res: any) => setCreateProjectForm({ ...res, startDate: dayjs(res.startDate), endDate: dayjs(res.endDate) }));
        }
    }, [projectId]);

    return (
        //Components for wizard steps
        <Box mb={7} border="solid 2px #ddd" p={4}>
            <Stepper activeStep={activeStep} alternativeLabel>
                {steps.map((label) => (
                    <Step key={label}>
                        <StepLabel StepIconComponent={CustomStepIcon}>{t(`createProject.${label}`)}</StepLabel>
                    </Step>
                ))}
            </Stepper>
            {activeStep === 0 && (
                <ProjectInformationForm validationError={validationError} setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />
            )}
            {activeStep === 1 && (
                <BlockHousesForm
                    validationError={validationError}
                    editForm={false}
                    createFormHouseBlock={createFormHouseBlock}
                    setCreateFormHouseBlock={setCreateFormHouseBlock}
                />
            )}
            {/* {activeStep === 2 && <div id={id} style={{ height: "70vh", width: "100%" }}></div>} */}

            <Stack direction="row" alignItems="center" justifyContent="flex-end" py={2}>
                <Button variant="outlined" onClick={() => handleSave()} sx={{ mr: 2 }}>
                    {t("generic.save")}
                </Button>
                <Button variant="contained" onClick={() => handleBack()} sx={{ mr: 2 }} disabled={activeStep === 0}>
                    {t("generic.previous")}
                </Button>
                <Button variant="contained" onClick={() => handleNext()} disabled={activeStep === 0 && !projectId}>
                    {t("generic.next")}
                </Button>
            </Stack>
        </Box>
    );
};
