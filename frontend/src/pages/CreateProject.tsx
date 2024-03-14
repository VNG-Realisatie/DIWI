import React, { useState } from "react";
import { Stepper, Step, StepLabel, Button, Box, Stack, Typography } from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import { ProjectInformationForm } from "../components/project/ProjectInformationForm";
import { BlockHousesForm } from "../components/BlockHousesForm";
import { SelectFromMapForm } from "../components/SelectFromMapForm";
import { TimelineForm } from "../components/TimelineForm";
import useAlert from "../hooks/useAlert";
import { useTranslation } from "react-i18next";
import { updateProject, createProject } from "../api/projectsServices";
import { useParams, useNavigate } from "react-router-dom";

const CustomStepIcon: React.FC<CustomStepIconProps> = ({ active, completed }) => {
    if (completed) {
        return <CheckCircleIcon color="primary" />;
    } else {
        return <RadioButtonUncheckedIcon color={active ? "primary" : "disabled"} />;
    }
};

const steps: string[] = ["information", "houseBlocks", "signupOnTheMap", "timeline"];

interface CustomStepIconProps {
    active: boolean;
    completed: boolean;
}
export const CreateProject = () => {
    const [createProjectForm, setCreateProjectForm] = useState<any>(null);

    const [activeStep, setActiveStep] = useState<number>(0);
    const [, setValidationError] = useState(false);

    const { id } = useParams();
    const navigate = useNavigate();

    const { setAlert } = useAlert();

    const { t } = useTranslation();

    const handleSave = async () => {
        if (!createProjectForm || !createProjectForm.projectName) {
            setValidationError(true);
            return;
        }
        try {
            if (id) {
                setValidationError(false);
                const res = await updateProject(id, createProjectForm);
                if (res.ok) {
                    setAlert(t("createProject.successfullySaved"), "success");
                    return true;
                }
            } else {
                setValidationError(false);
                const project = await createProject(createProjectForm);

                navigate(`/project/${project.projectId}`);
                setAlert(t("createProject.successfullySaved"), "success");
            }
        } catch (error: any) {
            setAlert(error.message, "error");
            return false;
        }
    };

    const handleNext = async () => {
        const res = await handleSave();
        if (res) {
            navigate(`/projects/${id}`); //before the next screen is implemented
            // setActiveStep((prevActiveStep) => prevActiveStep + 1);
        }
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

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
            {activeStep === 0 && <ProjectInformationForm setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />}
            {activeStep === 1 && <BlockHousesForm />}
            {activeStep === 2 && <SelectFromMapForm setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />}
            {activeStep === 3 && <TimelineForm setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />}
            {activeStep === 4 && (
                <Stack direction="row" justifyContent="center" p={5}>
                    <Typography> {t("createProject.completeMessage")}</Typography>
                </Stack>
            )}
            <Stack direction="row" alignItems="center" justifyContent="flex-end" py={2}>
                <Button variant="outlined" onClick={() => handleSave()} sx={{ mr: 2 }}>
                    {t("generic.save")}
                </Button>
                <Button variant="contained" onClick={() => handleBack()} sx={{ mr: 2 }} disabled={activeStep === 0}>
                    {t("generic.previous")}
                </Button>
                <Button variant="contained" onClick={() => handleNext()}>
                    {t("generic.next")}
                </Button>
            </Stack>
        </Box>
    );
};
