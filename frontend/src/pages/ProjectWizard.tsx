import { t } from "i18next";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { projectWizardBlocks, projectWizardWithId } from "../Paths";
import { createProject, getProject, updateProject } from "../api/projectsServices";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { ProjectInformationForm } from "../components/project/ProjectInformationForm";
import useAlert from "../hooks/useAlert";

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

    async function validateAndSave() {
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

                navigate(projectWizardWithId.toPath({ projectId: project.projectId }));
                setAlert(t("createProject.successfullySaved"), "success");
            }
        } catch (error: any) {
            setAlert(error.message, "error");
            return false;
        }
    }

    const handleSave = async () => {
        validateAndSave();
    };

    const handleNext = async () => {
        validateAndSave();
        if (projectId) {
            navigate(projectWizardBlocks.toPath({ projectId }));
        }
    };

    useEffect(() => {
        if (projectId) {
            getProject(projectId).then((res) => setCreateProjectForm({ ...res, startDate: res.startDate, endDate: res.endDate }));
        }
    }, [projectId]);

    const infoText = t("createProject.informationForm.info");

    return (
        <WizardLayout {...{ infoText, handleNext, handleSave, projectId, activeStep: 0 }}>
            <ProjectInformationForm setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />
        </WizardLayout>
    );
};

export default ProjectWizard;
