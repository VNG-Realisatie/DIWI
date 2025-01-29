import { PropsWithChildren, createContext, useCallback, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Project, getProject } from "../api/projectsServices";
import { components } from "../types/schema";
import { Property } from "../api/adminSettingServices";
import { useCustomPropertyStore } from "../hooks/useCustomPropertyStore";

export type ProjectType = null | components["schemas"]["ProjectListModel"];

type ProjectContextType = {
    selectedProject: Project | null;
    setSelectedProject(project: Project | null): void;
    projectId: string | undefined;
    updateProject(): void;
    nonFixedCustomDefinitions: Property[];
};

const ProjectContext = createContext<ProjectContextType | null>(null) as React.Context<ProjectContextType>;

export const ProjectProvider = ({ children }: PropsWithChildren) => {
    const { projectId } = useParams();
    const [selectedProject, setSelectedProject] = useState<Project | null>(null);
    const { projectCustomProperties } = useCustomPropertyStore();

    const nonFixedCustomDefinitions: Property[] = projectCustomProperties.filter((property) => !property.disabled && property.type !== "FIXED");

    const updateProject = useCallback(() => {
        if (projectId) {
            getProject(projectId).then((project) => setSelectedProject(project));
        }
    }, [projectId]);

    useEffect(() => {
        updateProject();
    }, [updateProject]);

    return (
        <ProjectContext.Provider
            value={{
                selectedProject,
                setSelectedProject,
                projectId,
                updateProject,
                nonFixedCustomDefinitions,
            }}
        >
            {children}
        </ProjectContext.Provider>
    );
};
export default ProjectContext;
