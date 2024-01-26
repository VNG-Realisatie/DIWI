import { PropsWithChildren, createContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Project, getProjects } from "../api/projectsServices";
type HouseBlockType = {
    id: number;
    naam: string | null;
    bruto_plancapaciteit: number;
    netto_plancapaciteit: number;
    sloop: number;
    mutatie_soort: string;
    buurt: null | string;
    wijk: string;
    fysiek_voorkomen: null | string;
    woning_type: null | {
        meergezinswoning?: number;
        eengezinswoning?: number;
    };
    eigendom_soort:
        | null
        | number
        | {
              koopwoning: number;
              huurwoning_woningcorporatie: number;
              huurwoning_particuliere_verhuurder: number;
          };
    waarde: null | string;
    huurbedrag: null | string;
    grootte: null | number;
    doelgroep: null | number;
    grondpositie: null | number;
    "start datum": string;
    "eind datum": string;
    woningblok_naam_changelog_id?: null | number;
    woningblok_mutatie_changelog_id?: null | number;
    woningblok_state_id?: null | number;
    woningblok_type_en_fysiek_voorkomen_changelog_id?: null | number;
    woningblok_eigendom_en_waarde_changelog_id?: null | number;
    woningblok_grootte_changelog_id?: null | number;
    woningblok_doelgroep_changelog_id?: null | number;
    woningblok_grondpositie_changelog_id?: null | number;
    project_id: number;
};
export type ProjectType = null | {
    id: any;
    name: string;
    eigenaar: string;
    "plan type": string | null;
    "eind datum": string | null;
    "start datum": string | null;
    priorisering: string | null;
    "project fase": string | null;
    "rol gemeente": string;
    programmering: boolean | null;
    "project leider": string | null;
    organization_id?: number | null;
    project_state_id?: number | null;
    organization_state_id?: number | null;
    vertrouwlijkheidsniveau: string;
    "planologische plan status": string | null;
    project_fase_changelog_id?: number | null;
    project_name_changelog_id?: number | null;
    project_gemeenterol_value_id?: number | null;
    project_priorisering_value_id?: number | null;
    project_gemeenterol_changelog_id?: number | null;
    project_priorisering_changelog_id?: number | null;
    project_gemeenterol_value_state_id?: number | null;
    project_priorisering_value_state_id?: number | null;
};
export type ProjectsType = {
    project: ProjectType;
    woningblokken: Array<HouseBlockType>;
};
type ProjectContextType = {
    selectedProject: Project | null;
    setSelectedProject(project: Project): void;
    projects: Array<Project>;
    setProjects(project: Array<Project>): void;
    id: string | undefined;
};
const ProjectContext = createContext<ProjectContextType | null>(null) as React.Context<ProjectContextType>;

export const ProjectProvider = ({ children }: PropsWithChildren) => {
    const { id } = useParams();
    const [projects, setProjects] = useState<Array<Project>>([]);
    const [selectedProject, setSelectedProject] = useState<Project | null>(null);

    useEffect(() => {
        if (id) {
            const findProjectByParamsId = projects.find((p) => p.projectId === id);
            findProjectByParamsId && setSelectedProject(findProjectByParamsId);
        }
    }, [id, projects]);

    useEffect(() => {
        getProjects()
            .then((projects) => setProjects(projects))
            .catch((err) => console.log(err));
    }, []);
    console.log(projects);
    return (
        <ProjectContext.Provider
            value={{
                selectedProject,
                setSelectedProject,
                projects,
                setProjects,
                id,
            }}
        >
            {children}
        </ProjectContext.Provider>
    );
};
export default ProjectContext;
