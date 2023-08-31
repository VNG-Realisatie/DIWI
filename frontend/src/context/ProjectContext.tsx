import { PropsWithChildren, createContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
// import { projects } from "../api/dummyData";
import projectData from "../api/json/projects.json";
type HouseBlockType = {
    id: number;
    naam: string | null;
    bruto_plancapaciteit: number;
    netto_plancapaciteit: number;
    sloop: number;
    mutatie_soort: string;
    buurt: null | string;
    wijk:  string;
    fysiek_voorkomen: null | string;
    woning_type: null | {
        meergezinswoning?: number;
        eengezinswoning?: number;
    };
    eigendom_soort: null | number | {
        koopwoning: number;
        huurwoning_woningcorporatie:number;
        huurwoning_particuliere_verhuurder:number;
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
export type ProjectType=null|{
    id: number;
    name: string;
    eigenaar: string;
    "plan type": string | null;
    "eind datum": string | null;
    "start datum": string | null;
    priorisering: string | null;
    "project fase": string | null;
    "rol gemeente": string;
    programmering: boolean | null;
    "project leider": string;
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
}
export type ProjectsType = {
    project: ProjectType;
    woningblokken: Array<HouseBlockType>;
};
type ProjectContextType = {
    selectedProject: ProjectType|undefined;
    setSelectedProject(project: ProjectType): void;
    projects: Array<ProjectsType>;
    setProjects(project: Array<ProjectsType>): void;
    id:string|undefined;
};
const ProjectContext = createContext<ProjectContextType| null>(null) as React.Context<ProjectContextType>

export const ProjectProvider = ({ children }: PropsWithChildren) => {
    const { id } = useParams();
    const findProjectByParamsId=projectData.find((p) => id && p.project.id === parseInt(id))?.project
    const [projects, setProjects] = useState<Array<any>>(projectData);
    const [selectedProject, setSelectedProject] = useState<ProjectType|undefined>(
        findProjectByParamsId
    );

    useEffect(() => {
        setSelectedProject(
            findProjectByParamsId
        );
    }, [findProjectByParamsId, id]);
    return (
        <ProjectContext.Provider
            value={{
                selectedProject,
                setSelectedProject,
                projects,
                setProjects,
                id
            }}
        >
            {children}
        </ProjectContext.Provider>
    );
};
export default ProjectContext;
