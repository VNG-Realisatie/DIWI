import { useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { HouseBlock } from "../components/project-wizard/house-blocks/types";
import { projectWizardMap, projectWizardWithId } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { HouseBlocksForm } from "../components/HouseBlocksForm";
import useAlert from "../hooks/useAlert";
import HouseBlockContext from "../context/HouseBlockContext";
import { t } from "i18next";

const ProjectWizardBlocks = () => {
    const { houseBlocks, addHouseBlock, getEmptyHouseBlock, updateHouseBlock } = useContext(HouseBlockContext);
    const [houseBlock, setHouseBlock] = useState<HouseBlock>(getEmptyHouseBlock());
    const { projectId } = useParams();
    const navigate = useNavigate();
    const { setAlert } = useAlert();

    const handleNext = async () => {
        if (
            !houseBlock.houseblockName ||
            !houseBlock.startDate ||
            !houseBlock.endDate ||
            houseBlock.ownershipValue.some((owner) => owner.amount === null || isNaN(owner.amount))
        ) {
            setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
        } else {
            if (houseBlock.houseblockId) {
                updateHouseBlock(houseBlock);
            } else {
                addHouseBlock(houseBlock);
            }
            if (projectId) {
                navigate(projectWizardMap.toPath({ projectId }));
            }
        }
    };

    const handleSave = async () => {
        if (houseBlock.houseblockId) {
            updateHouseBlock(houseBlock);
        } else {
            addHouseBlock(houseBlock);
        }
    };

    const handleBack = () => {
        if (projectId) {
            navigate(projectWizardWithId.toPath({ projectId }));
        }
    };

    useEffect(() => {
        const filteredHouseBlocks = houseBlocks.filter((hb) => hb.houseblockName === houseBlock.houseblockName);
        if (filteredHouseBlocks.length > 0) {
            setHouseBlock(filteredHouseBlocks[0]);
        }
    }, [houseBlock.houseblockName, houseBlocks, setHouseBlock]);

    const infoText = t("createProject.houseBlocksForm.info");

    return (
        <WizardLayout {...{ infoText, handleBack, handleNext, handleSave, projectId, activeStep: 1 }}>
            <HouseBlocksForm readOnly={false} houseBlock={houseBlock} setHouseBlock={setHouseBlock} />
        </WizardLayout>
    );
};

export default ProjectWizardBlocks;
