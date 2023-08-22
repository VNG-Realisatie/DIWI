import { PropsWithChildren, createContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { projects } from "../api/dummyData";

type ProjectContextType = {
  selectedProject: any;
  setSelectedProject(project: any): void;
};
const ProjectContext = createContext<ProjectContextType>({
  selectedProject: {},
  setSelectedProject: (project: any) => {},
});

export const ProjectProvider = ({ children }: PropsWithChildren) => {
    const {id}=useParams();
  const [selectedProject, setSelectedProject] = useState<any >(projects.find(p=> id&&p.id===parseInt(id)));

useEffect(()=>{

    setSelectedProject(projects.find(p=> id&&p.id===parseInt(id)))
},[id])
  return (
    <ProjectContext.Provider
      value={{
        selectedProject,
        setSelectedProject,
      }}
    >
      {children}
    </ProjectContext.Provider>
  );
};
export default ProjectContext
