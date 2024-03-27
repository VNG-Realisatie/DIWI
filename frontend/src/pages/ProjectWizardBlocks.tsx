import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { HouseBlock } from "../components/project-wizard/house-blocks/types";
import { emptyHouseBlockForm } from "../components/project-wizard/house-blocks/constants";
import { addHouseBlock } from "../api/projectsServices";
import { projectWizardMap, projectWizardWithId } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { HouseBlocksForm } from "../components/HouseBlocksForm";
import useAlert from "../hooks/useAlert";
import { useTranslation } from "react-i18next";

const ProjectWizardBlocks = () => {
    const [createFormHouseBlock, setCreateFormHouseBlock] = useState<HouseBlock>(emptyHouseBlockForm);
    const [validationError, setValidationError] = useState("");
    const { projectId } = useParams();
    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const { t } = useTranslation();

    const handleNext = async () => {
        try {
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
        } catch (error: any) {
            setAlert(error.message, "warning");
        }
    };

    const handleSave = async () => {
        try {
            await addHouseBlock({ ...createFormHouseBlock, projectId });
            setAlert(t("createProject.houseBlocksForm.notifications.successfullySaved"), "success");
        } catch (error: any) {
            setAlert(error.message, "warning");
        }
    };

    const handleBack = () => {
        navigate(projectWizardWithId.toPath({ projectId }));
    };

    return (
        <WizardLayout {...{ handleBack, handleNext, handleSave, projectId, activeStep: 1 }}>
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
