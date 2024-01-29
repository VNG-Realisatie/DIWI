import React, { useState } from "react";
import { Stepper, Step, StepLabel, Button, Box, Stack, Typography } from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import { ProjectInformationForm } from "../components/ProjectInformationForm";
import { BlockHousesForm } from "../components/BlockHousesForm";
import { SelectFromMapForm } from "../components/SelectFromMapForm";
import { TimelineForm } from "../components/TimelineForm";
import useAlert from "../hooks/useAlert";
import { useTranslation } from "react-i18next";

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
    const [createHouseBlockForm, setCreateHouseBlockForm] = useState<any>(null);
    const [activeStep, setActiveStep] = useState<number>(0);

    const { setAlert } = useAlert();

    const { t } = useTranslation();

    const handleSave = () => {
        //Todo add createendpoint here
        setAlert(t("createProject.successfullySaved"), "success");
    };

    const handleNext = () => {
        setActiveStep((prevActiveStep) => prevActiveStep + 1);
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
            {activeStep === 1 && <BlockHousesForm setCreateProjectForm={setCreateHouseBlockForm} createProjectForm={createHouseBlockForm} />}
            {activeStep === 2 && <SelectFromMapForm setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />}
            {activeStep === 3 && <TimelineForm setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />}
            {activeStep === 4 && (
                <Stack direction="row" justifyContent="center" p={5}>
                    <Typography> {t("createProject.completeMessage")}</Typography>
                </Stack>
            )}
            <Stack direction="row" alignItems="center" justifyContent="flex-end" py={2}>
                <Button variant="contained" onClick={() => handleBack()} sx={{ mr: 2 }} disabled={activeStep === 0}>
                    {t("generic.previous")}
                </Button>
                <Button
                    variant="contained"
                    onClick={() => (activeStep === steps.length ? handleSave() : handleNext())} // check last step save function
                >
                    {activeStep === steps.length ? t("generic.save") : t("generic.next")}
                </Button>
            </Stack>
        </Box>
    );
};
