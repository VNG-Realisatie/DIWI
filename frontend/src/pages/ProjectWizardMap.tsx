import { useId } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { projectDetailCharacteristics, projectWizardBlocks } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import usePlotSelector from "../hooks/usePlotSelector";
import { t } from "i18next";

export const ProjectWizardMap = () => {
    const { projectId } = useParams();
    const navigate = useNavigate();
    const id = useId();
    const { selectedPlotCount, handleSaveChange } = usePlotSelector(id);

    const handleNext = async () => {
        handleSaveChange();
        if (projectId) {
            navigate(projectDetailCharacteristics.toPath({ projectId }));
        }
    };

    const handleBack = () => {
        if (projectId) {
            navigate(projectWizardBlocks.toPath({ projectId }));
        }
    };

    const infoText = t("createProject.selectMapForm.info");
    const warning = selectedPlotCount <= 0 ? t("createProject.selectMapForm.warning") : undefined;

    return (
        <WizardLayout {...{ infoText, warning, handleBack, handleNext, handleSave: handleSaveChange, projectId, activeStep: 2 }}>
            <div id={id} style={{ height: "60vh", width: "100%" }}></div>
        </WizardLayout>
    );
};
