import { useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { HouseBlock } from "../components/project-wizard/house-blocks/types";
import { projectWizardMap, projectWizardWithId } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { HouseBlocksForm } from "../components/HouseBlocksForm";
import useAlert from "../hooks/useAlert";
import { useTranslation } from "react-i18next";
import HouseBlockContext from "../context/HouseBlockContext";

const ProjectWizardBlocks = () => {
    const { houseBlocks, addHouseBlock, getEmptyHouseBlock, updateHouseBlock } = useContext(HouseBlockContext);
    const [houseBlock, setHouseBlock] = useState<HouseBlock>(getEmptyHouseBlock());
    const { projectId } = useParams();
    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const { t } = useTranslation();

    useEffect(() => {
        const fetchHouseBlocks = async () => {
            const data = await getProjectHouseBlocks(projectId as string);
            setHouseBlocks(data);
        };

        fetchHouseBlocks();
    }, [projectId]);

    useEffect(() => {
        if (houseBlocks && houseBlocks.length === 0) {
            const fetchProject = async () => {
                const data = await getProject(projectId as string);
                setProject(data);
            };

            fetchProject();
        }
    }, [projectId, houseBlocks]);

    useEffect(() => {
        if (houseBlocks && houseBlocks.length > 0) {
            let earlierCreatedHouseBlock;

            houseBlocks.forEach((properties) => {
                earlierCreatedHouseBlock = { ...properties };

                setCreateFormHouseBlock(earlierCreatedHouseBlock);
            });
        }
    }, [houseBlocks]);

    const setDates = useCallback(() => {
        if (project && project.startDate && project.endDate) {
            setCreateFormHouseBlock({
                ...emptyHouseBlockForm,
                startDate: project.startDate,
                endDate: project.endDate,
            });
        }
    }, [project, setCreateFormHouseBlock]);

    useEffect(() => {
        if (project) {
            setDates();
        }
    }, [project, setDates]);

    const handleNext = async () => {
        if (
            !houseBlock.houseblockName ||
            !houseBlock.startDate ||
            !houseBlock.endDate ||
            houseBlock.ownershipValue.some((owner) => owner.amount === null || isNaN(owner.amount))
        ) {
            setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
        } else {
            addHouseBlock(houseBlock);
            navigate(projectWizardMap.toPath({ projectId }));
        }
    };

    const handleSave = async () => {
        try {
            if (houseBlock.houseblockId) {
                updateHouseBlock(houseBlock);
                setAlert(t("createProject.houseBlocksForm.notifications.successfullyUpdated"), "success");
            } else {
                addHouseBlock(houseBlock);
                setAlert(t("createProject.houseBlocksForm.notifications.successfullySaved"), "success");
            }
        } catch (error: any) {
            setAlert(error.message, "warning");
        }
    };

    const handleBack = () => {
        navigate(projectWizardWithId.toPath({ projectId }));
    };

    useEffect(() => {
        const filteredHouseBlocks = houseBlocks.filter((hb) => hb.houseblockName === houseBlock.houseblockName);
        if (filteredHouseBlocks.length > 0) {
            setHouseBlock(filteredHouseBlocks[0]);
        }
    }, [houseBlock.houseblockName, houseBlocks, setHouseBlock]);

    return (
        <WizardLayout {...{ handleBack, handleNext, handleSave, projectId, activeStep: 1 }}>
            <HouseBlocksForm readOnly={false} houseBlock={houseBlock} setHouseBlock={setHouseBlock} />
        </WizardLayout>
    );
};

export default ProjectWizardBlocks;
