import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { HouseBlock } from "../components/project-wizard/house-blocks/types";
import { emptyHouseBlockForm } from "../components/project-wizard/house-blocks/constants";
import { Project, addHouseBlock, getProject, getProjectHouseBlocks, updateHouseBlock } from "../api/projectsServices";
import { projectWizardMap, projectWizardWithId } from "../Paths";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { HouseBlocksForm } from "../components/HouseBlocksForm";
import useAlert from "../hooks/useAlert";
import { useTranslation } from "react-i18next";

const ProjectWizardBlocks = () => {
    const [createFormHouseBlock, setCreateFormHouseBlock] = useState<HouseBlock>(emptyHouseBlockForm);
    const [validationError, setValidationError] = useState("");
    const [project, setProject] = useState<Project>();
    const [houseBlocks, setHouseBlocks] = useState<HouseBlock[]>();
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
            let earlierCreatedHouseBlock = { ...emptyHouseBlockForm };

            houseBlocks.forEach((property) => {
                earlierCreatedHouseBlock = { ...earlierCreatedHouseBlock, ...property };
            });

            setCreateFormHouseBlock(earlierCreatedHouseBlock);
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
            if (createFormHouseBlock.houseblockId) {
                await updateHouseBlock({ ...createFormHouseBlock, projectId });
            } else {
                await addHouseBlock({ ...createFormHouseBlock, projectId });
            }

            navigate(projectWizardMap.toPath({ projectId }));
        } catch (error: any) {
            setAlert(error.message, "warning");
        }
    };

    const handleSave = async () => {
        try {
            if (createFormHouseBlock.houseblockId) {
                await updateHouseBlock({ ...createFormHouseBlock, projectId });
            } else {
                await addHouseBlock({ ...createFormHouseBlock, projectId });
            }
            setAlert(t("createProject.houseBlocksForm.notifications.successfullySaved"), "success");
        } catch (error: any) {
            setAlert(error.message, "warning");
        }
    };

    const handleBack = () => {
        navigate(projectWizardWithId.toPath({ projectId }));
    };

    console.log(houseBlocks);

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
