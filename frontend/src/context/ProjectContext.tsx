import { PropsWithChildren, createContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Project, getProjects } from "../api/projectsServices";
import { components } from "../types/schema";
import { GridPaginationModel } from "@mui/x-data-grid";

export type ProjectType = null | components["schemas"]["ProjectListModel"];

type ProjectContextType = {
    selectedProject: Project | null;
    setSelectedProject(project: Project): void;
    projects: Array<Project>;
    setProjects(project: Array<Project>): void;
    id: string | undefined;
    paginationInfo: GridPaginationModel;
    setPaginationInfo(info: GridPaginationModel): void;
};

const ProjectContext = createContext<ProjectContextType | null>(null) as React.Context<ProjectContextType>;

export const ProjectProvider = ({ children }: PropsWithChildren) => {
    const { id } = useParams();
    const [projects, setProjects] = useState<Array<Project>>([]);
    const [selectedProject, setSelectedProject] = useState<Project | null>(null);
    const [paginationInfo, setPaginationInfo] = useState<GridPaginationModel>({ page: 1, pageSize: 10 });
    useEffect(() => {
        if (id) {
            const findProjectByParamsId = projects.find((p) => p.projectId === id);
            findProjectByParamsId && setSelectedProject(findProjectByParamsId);
        }
    }, [id, projects]);

    useEffect(() => {
        getProjects(paginationInfo.page, paginationInfo.pageSize)
            .then((projects) => setProjects(projects))
            .catch((err) => console.log(err));
    }, [paginationInfo.page, paginationInfo.pageSize]);

    return (
        <ProjectContext.Provider
            value={{
                selectedProject,
                setSelectedProject,
                projects,
                setProjects,
                id,
                paginationInfo,
                setPaginationInfo,
            }}
        >
            {children}
        </ProjectContext.Provider>
    );
};
export default ProjectContext;
