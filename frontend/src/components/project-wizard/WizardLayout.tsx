import { Alert, Box, Button, Stack, Step, StepLabel, Stepper } from "@mui/material";
import { t } from "i18next";

import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import RadioButtonChecked from "@mui/icons-material/RadioButtonChecked";
import { TooltipInfo } from "../../widgets/TooltipInfo";

const CustomStepIcon: React.FC<CustomStepIconProps> = ({ active, completed }) => {
    if (completed) {
        return <CheckCircleIcon color="primary" />;
    } else if (active) {
        return <RadioButtonChecked color="primary" />;
    } else {
        return <RadioButtonUncheckedIcon color="disabled" />;
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
type Props = {
    children: React.ReactNode;
    handleBack?: () => void;
    handleNext: () => void;
    handleSave: () => void;
    activeStep: number;
    projectId: string | undefined;
    infoText: string;
    warning?: string;
};

const WizardLayout = ({ children, handleBack, handleNext, handleSave, activeStep, projectId, infoText, warning }: Props) => {
    return (
        <Box mb={7} border="solid 2px #ddd" p={4}>
            <Stepper activeStep={activeStep} alternativeLabel>
                {steps.map((label) => (
                    <Step key={label}>
                        <StepLabel StepIconComponent={CustomStepIcon}>{t(`createProject.${label}`)}</StepLabel>
                    </Step>
                ))}
            </Stepper>
            <Alert severity="info">
                {infoText}
                {activeStep === 1 && <TooltipInfo text={t("tooltipInfo.woningblok.title")} />}
                {activeStep === 2 && <TooltipInfo text={t("tooltipInfo.selecteerPercelen.title")} />}
            </Alert>
            {children}
            <Stack direction="row" alignItems="center" justifyContent="flex-end" py={2}>
                {warning && (
                    <Alert severity="warning" sx={{ mr: 2 }}>
                        {warning}
                    </Alert>
                )}
                <Button variant="outlined" onClick={() => handleSave()} sx={{ mr: 2 }}>
                    {t("generic.save")}
                </Button>
                <Button variant="contained" onClick={() => (handleBack ? handleBack() : null)} sx={{ mr: 2 }} disabled={activeStep === 0}>
                    {t("generic.previous")}
                </Button>
                <Button variant="contained" onClick={() => handleNext()} disabled={activeStep === 0 && !projectId}>
                    {t("generic.next")}
                </Button>
            </Stack>
        </Box>
    );
};

export default WizardLayout;
