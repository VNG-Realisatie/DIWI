import React, { useState } from "react";
import {
  Stepper,
  Step,
  StepLabel,
  Button,
  Box,
  Stack,
  Typography,
} from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import { ProjectInformationForm } from "../components/ProjectInformationForm";
import { BlockHousesForm } from "../components/BlockHousesForm";
import { SelectFromMapForm } from "../components/SelectFromMapForm";
import { TimelineForm } from "../components/TimelineForm";
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
  const [createHouseBlockForm, setCreateHouseBlockForm] = useState<any>(null);
  const [activeStep, setActiveStep] = useState<number>(0);
  const handleSave = () => {
    //Todo add createendpoint here
    console.log("Saved");
  };

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };
  console.log(createProjectForm);
  return (
    //Components for wizard steps
    <Box  mb={7} border="solid 2px #ddd" p={4}>
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
      {activeStep === 1 && (
        <BlockHousesForm
          setCreateProjectForm={setCreateHouseBlockForm}
          createProjectForm={createHouseBlockForm}
        />
      )}
      {activeStep === 2 && (
        <SelectFromMapForm
          setCreateProjectForm={setCreateProjectForm}
          createProjectForm={createProjectForm}
        />
      )}
      {activeStep === 3 && (
        <TimelineForm
          setCreateProjectForm={setCreateProjectForm}
          createProjectForm={createProjectForm}
        />
      )}
      {activeStep === 4 && (
        <Stack direction="row" justifyContent="center" p={5}>
          <Typography>
            De wizard voor het maken van projectformulieren is voltooid. Klik op
            Opslaan om het proces te voltooien.
          </Typography>
        </Stack>
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
          onClick={() =>
            activeStep === steps.length ? handleSave() : handleNext()
          } // check last step save function
        >
          {activeStep === steps.length ? "Opslaan" : "Volgende"}
        </Button>
      </Stack>
    </Box>
  );
};
