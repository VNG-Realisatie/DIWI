import { useId } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { projectDetailCharacteristics, projectWizardWithId } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import usePlotSelector from "../hooks/usePlotSelector";

export const ProjectWizardMap = () => {
    const { projectId } = useParams();
    const navigate = useNavigate();
    const id = useId();
    const { handleSaveChange } = usePlotSelector(id);

    const handleNext = async () => {
        handleSaveChange();
        navigate(projectDetailCharacteristics.toPath({ id: projectId }));
    };

    const handleBack = () => {
        navigate(projectWizardWithId.toPath({ id: projectId }));
    };

    return (
        <WizardLayout {...{ handleBack, handleNext, handleSave: handleSaveChange, projectId, activeStep: 2 }}>
            <div id={id} style={{ height: "60vh", width: "100%" }}></div>
        </WizardLayout>
    );
};
