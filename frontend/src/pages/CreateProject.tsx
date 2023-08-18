import React, { useState } from "react";
import { Stepper, Step, StepLabel, Button, Box, Stack } from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import { ProjectInformationForm } from "../components/ProjectInformationForm";
const CustomStepIcon: React.FC<CustomStepIconProps> = ({
  active,
  completed,
}) => {
  if (completed) {
    return <CheckCircleIcon color="primary" />;
  } else {
    return <RadioButtonUncheckedIcon color={active ? "primary" : "disabled"} />;
  }
};

const steps: string[] = [
  "Project informatie",
  "Huizen blokken",
  "Intekenen op de kaart",
  "Tijdlijn",
];

interface CustomStepIconProps {
  active: boolean;
  completed: boolean;
}
export const CreateProject = () => {
  const [createProjectForm, setCreateProjectForm] = useState<any>(null);
  const [activeStep, setActiveStep] = useState<number>(0);
  const handleSave=()=>{
    //Todo add createendpoint here
    console.log("Saved")
  }

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };
  console.log(createProjectForm);
  return (
    <Box mt={2} border="solid 1px #ddd" p={4}>
      <Stepper activeStep={activeStep} alternativeLabel>
        {steps.map((label, index) => (
          <Step key={label}>
            <StepLabel StepIconComponent={CustomStepIcon}>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
      {activeStep === 0 && (
        <ProjectInformationForm
          setCreateProjectForm={setCreateProjectForm}
          createProjectForm={createProjectForm}
        />
      )}
      <Stack
        direction="row"
        alignItems="center"
        justifyContent="flex-end"
        py={2}
      >
        <Button
          variant="contained"
          onClick={() => handleBack()}
          sx={{ mr: 2 }}
          disabled={activeStep === 0}
        >
          Vorig
        </Button>
        <Button
          variant="contained"
          onClick={() => activeStep === steps.length?handleSave():handleNext()}
        >
         {activeStep === steps.length? "Opslaan":"Volgende"} 
        </Button>
      </Stack>
    </Box>
  );
};
