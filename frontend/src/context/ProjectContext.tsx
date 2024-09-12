import { PropsWithChildren, createContext, useCallback, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Project, getProject, getProjects, getProjectsSize } from "../api/projectsServices";
import { components } from "../types/schema";
import { GridPaginationModel } from "@mui/x-data-grid";

export type ProjectType = null | components["schemas"]["ProjectListModel"];

type ProjectContextType = {
    selectedProject: Project | null;
    setSelectedProject(project: Project | null): void;
    projects: Array<Project>;
    setProjects(project: Array<Project>): void;
    totalProjectCount: number;
    projectId: string | undefined;
    paginationInfo: GridPaginationModel;
    setPaginationInfo(info: GridPaginationModel): void;
    updateProject(): void;
    updateProjects(): void;
};

const ProjectContext = createContext<ProjectContextType | null>(null) as React.Context<ProjectContextType>;

export const ProjectProvider = ({ children }: PropsWithChildren) => {
    const { projectId } = useParams();
    const [projects, setProjects] = useState<Array<Project>>([]);
    const [selectedProject, setSelectedProject] = useState<Project | null>(null);
    const [paginationInfo, setPaginationInfo] = useState<GridPaginationModel>({ page: 1, pageSize: 10 });
    const [totalProjectCount, setTotalProjectCount] = useState(0);

    const updateProjects = useCallback(() => {
        getProjects(paginationInfo.page, paginationInfo.pageSize)
            .then((projects) => setProjects(projects))
            .catch((err) => console.log(err));
        getProjectsSize()
            .then((data) => {
                setTotalProjectCount(data.size);
            })
            .catch((err) => console.log(err));
    }, [paginationInfo.page, paginationInfo.pageSize]);

    const updateProject = useCallback(() => {
        if (projectId) {
            getProject(projectId).then((project) => setSelectedProject(project));
        }
    }, [projectId]);

    useEffect(() => {
        updateProject();
    }, [updateProject]);

    useEffect(() => {
        updateProjects();
    }, [updateProjects]);

    return (
        <ProjectContext.Provider
            value={{
                selectedProject,
                setSelectedProject,
                projects,
                setProjects,
                totalProjectCount,
                projectId,
                paginationInfo,
                setPaginationInfo,
                updateProject,
                updateProjects,
            }}
        >
            {children}
        </ProjectContext.Provider>
    );
};
export default ProjectContext;
