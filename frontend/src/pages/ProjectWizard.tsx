import { t } from "i18next";
import { useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { projectWizardBlocks, projectWizardWithId } from "../Paths";
import { Project, createProject, getProject, updateProject } from "../api/projectsServices";
import WizardLayout from "../components/project-wizard/WizardLayout";
import useAlert from "../hooks/useAlert";
import ProjectContext from "../context/ProjectContext";
import { ProjectForm } from "../components/ProjectForm";

const ProjectWizard = () => {
    const [projectForm, setProjectForm] = useState<Project>({
        projectColor: "#FF5733",
        projectLeaders: [],
        projectOwners: [],
        projectPhase: "_1_CONCEPT",
        planningPlanStatus: [],
        startDate: undefined,
        endDate: undefined,
        projectName: "",
        projectId: "temp_id",
        confidentialityLevel: "PRIVE",
        customProperties: [],
    });
    const { projectId } = useParams();
    const navigate = useNavigate();
    const { setAlert } = useAlert();
    const { updateProjects } = useContext(ProjectContext);

    async function validateAndSave() {
        if (
            !projectForm.projectName ||
            !projectForm.startDate ||
            !projectForm.endDate ||
            !projectForm.projectColor ||
            !projectForm.projectPhase ||
            !projectForm.confidentialityLevel
        ) {
            setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
            return false;
        }
        try {
            // when saved initially we can keep updating existing project
            if (projectId) {
                await updateProject(projectForm);
                setAlert(t("createProject.successfullySaved"), "success");
                return true;
            } else {
                // for initial save only subset of attributes is used
                const temporaryCreateForm = {
                    projectName: projectForm.projectName,
                    projectColor: projectForm.projectColor,
                    projectPhase: projectForm.projectPhase,
                    confidentialityLevel: projectForm.confidentialityLevel,
                    startDate: projectForm.startDate,
                    endDate: projectForm.endDate,
                };
                const project = await createProject(temporaryCreateForm);
                // after save immediately update Id and send attibutes that have not been saved yet
                projectForm.projectId = project.projectId;
                await updateProject(projectForm);
                setAlert(t("createProject.successfullySaved"), "success");

                navigate(projectWizardWithId.toPath({ projectId: project.projectId }));
            }
            updateProjects();
        } catch (error: any) {
            setAlert(error.message, "error");
            return false;
        }
    }

    const handleSave = async () => {
        validateAndSave();
    };

    const handleNext = async () => {
        const validateAndSaveSuccess = await validateAndSave();
        if (validateAndSaveSuccess && projectId) {
            navigate(projectWizardBlocks.toPath({ projectId }));
        }
    };

    useEffect(() => {
        if (projectId) {
            getProject(projectId).then((res) => setProjectForm({ ...res, startDate: res.startDate, endDate: res.endDate }));
        }
    }, [projectId]);

    const infoText = t("createProject.informationForm.info");

    return (
        <WizardLayout {...{ infoText, handleNext, handleSave, projectId, activeStep: 0 }}>
            <ProjectForm project={projectForm} setProject={setProjectForm} readOnly={false} showColorPicker={true} />
        </WizardLayout>
    );
};

export default ProjectWizard;
