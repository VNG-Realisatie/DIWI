import { t } from "i18next";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { projectWizardBlocks, projectWizardWithId } from "../Paths";
import { Project, createProject, getProject, updateProject } from "../api/projectsServices";
import WizardLayout from "../components/project-wizard/WizardLayout";
import { ProjectInformationForm } from "../components/project/ProjectInformationForm";
import useAlert from "../hooks/useAlert";

const ProjectWizard = () => {
    const [createProjectForm, setCreateProjectForm] = useState<Partial<Project>>({
        projectColor: "#FF5733",
        projectLeaders: [],
        projectOwners: [],
        projectPhase: "_1_INITIATIEFFASE",
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
                await updateProject(createProjectForm as Project);
                setAlert(t("createProject.successfullySaved"), "success");
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
                await updateProject(createProjectForm as Project);
                setAlert(t("createProject.successfullySaved"), "success");

                navigate(projectWizardWithId.toPath({ projectId: project.projectId }));
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
