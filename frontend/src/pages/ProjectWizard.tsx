import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useAlert from "../hooks/useAlert";
import { createProject, getProject, updateProject } from "../api/projectsServices";
import dayjs from "dayjs";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { ProjectInformationForm } from "../components/project/ProjectInformationForm";
import { projectWizardBlocks } from "../Paths";
import { t } from "i18next";

const ProjectWizard = () => {
    const [createProjectForm, setCreateProjectForm] = useState<any>({
        projectColor: "#FF5733",
        projectLeaders: [],
        projectOwners: [],
        projectPhase: "",
        planningPlanStatus: [],
    });
    const { projectId } = useParams();
    const navigate = useNavigate();
    const { setAlert } = useAlert();

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
        navigate(projectWizardBlocks.toPath({ projectId }));
    };

    useEffect(() => {
        if (projectId) {
            getProject(projectId).then((res: any) => setCreateProjectForm({ ...res, startDate: dayjs(res.startDate), endDate: dayjs(res.endDate) }));
        }
    }, [projectId]);

    const infoText = t("createProject.informationForm.info");

    return (
        <WizardLayout {...{ infoText, warning: undefined, handleNext, handleSave, projectId, activeStep: 0 }}>
            <ProjectInformationForm setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />
        </WizardLayout>
    );
};

export default ProjectWizard;
