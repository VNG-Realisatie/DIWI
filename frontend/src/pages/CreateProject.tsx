import dayjs from "dayjs";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate, useParams } from "react-router-dom";
import { addHouseBlock, createProject, getProject, updateProject } from "../api/projectsServices";
import { HouseBlocksForm } from "../components/HouseBlocksForm";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { emptyHouseBlockForm } from "../components/project-wizard/house-blocks/constants";
import { HouseBlock } from "../components/project-wizard/house-blocks/types";
import { ProjectInformationForm } from "../components/project/ProjectInformationForm";
import useAlert from "../hooks/useAlert";
import { projectWizardMap } from "../Paths";

export const CreateProject = () => {
    const [createProjectForm, setCreateProjectForm] = useState<any>({
        projectColor: "#FF5733",
        projectLeaders: [],
        projectOwners: [],
        projectPhase: "",
        planningPlanStatus: [],
    });
    const [createFormHouseBlock, setCreateFormHouseBlock] = useState<HouseBlock>(emptyHouseBlockForm);
    const [activeStep, setActiveStep] = useState<number>(0);
    const [validationError, setValidationError] = useState("");

    const { id: projectId } = useParams();
    const navigate = useNavigate();

    const { setAlert } = useAlert();

    const { t } = useTranslation();

    const handleSave = async () => {
        if (
            !createProjectForm.projectName ||
            !createProjectForm.startDate ||
            !createProjectForm.endDate ||
            !createProjectForm.projectColor ||
            !createProjectForm.projectPhase ||
            !createProjectForm.confidentialityLevel
        ) {
            setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
            return;
        }
        try {
            if (projectId) {
                setValidationError("");
                const res = await updateProject(projectId, createProjectForm);
                if (res.ok) {
                    setAlert(t("createProject.successfullySaved"), "success");
                    return true;
                }
            } else {
                const temporaryCreateForm = {
                    projectName: createProjectForm.projectName,
                    projectColor: createProjectForm.projectColor,
                    projectPhase: createProjectForm.projectPhase,
                    confidentialityLevel: createProjectForm.confidentialityLevel,
                    startDate: createProjectForm.startDate,
                    endDate: createProjectForm.endDate,
                };
                setValidationError("");
                const project = await createProject(temporaryCreateForm);
                createProjectForm.projectId = project.projectId;
                await updateProject(createProjectForm.projectId, createProjectForm);

                navigate(`/project/create/${project.projectId}`);
                setAlert(t("createProject.successfullySaved"), "success");
            }
        } catch (error: any) {
            setAlert(error.message, "error");
            return false;
        }
    };

    const handleNext = async () => {
        if (activeStep === 0) {
            navigate(`/project/create/${projectId}/blocks`);
        }
        if (activeStep === 1) {
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
            return;
        }

        setActiveStep((prevActiveStep) => prevActiveStep + 1);
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    useEffect(() => {
        if (projectId) {
            getProject(projectId).then((res: any) => setCreateProjectForm({ ...res, startDate: dayjs(res.startDate), endDate: dayjs(res.endDate) }));
        }
    }, [projectId]);

    return (
        //Components for wizard steps
        <WizardLayout {...{ handleBack, handleNext, handleSave, projectId, activeStep }}>
            {activeStep === 0 && <ProjectInformationForm setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />}
            {activeStep === 1 && (
                <HouseBlocksForm
                    validationError={validationError}
                    editForm={false}
                    createFormHouseBlock={createFormHouseBlock}
                    setCreateFormHouseBlock={setCreateFormHouseBlock}
                />
            )}

            {/* {activeStep === 2 && <div id={id} style={{ height: "70vh", width: "100%" }}></div>} */}
        </WizardLayout>
    );
};
