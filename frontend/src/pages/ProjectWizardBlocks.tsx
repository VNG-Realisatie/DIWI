import { useId, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import usePlotSelector from "../hooks/usePlotSelector";
import { HouseBlock } from "../components/project-wizard/house-blocks/types";
import { emptyHouseBlockForm } from "../components/project-wizard/house-blocks/constants";
import { addHouseBlock } from "../api/projectsServices";
import { projectWizardMap, projectWizardWithId } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { HouseBlocksForm } from "../components/HouseBlocksForm";

const ProjectWizardBlocks = () => {
    const [createFormHouseBlock, setCreateFormHouseBlock] = useState<HouseBlock>(emptyHouseBlockForm);
    const [validationError, setValidationError] = useState("");

    const { projectId } = useParams();
    const navigate = useNavigate();
    const id = useId();
    const { handleSaveChange } = usePlotSelector(id);

    const handleNext = async () => {
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

        navigate(projectWizardMap.toPath({ projectId }));
    };

    const handleBack = () => {
        navigate(projectWizardWithId.toPath({ projectId }));
    };
    return (
        <WizardLayout {...{ handleBack, handleNext, handleSave: handleSaveChange, projectId, activeStep: 1 }}>
            <HouseBlocksForm
                validationError={validationError}
                editForm={false}
                createFormHouseBlock={createFormHouseBlock}
                setCreateFormHouseBlock={setCreateFormHouseBlock}
            />
        </WizardLayout>
    );
};

export default ProjectWizardBlocks;
