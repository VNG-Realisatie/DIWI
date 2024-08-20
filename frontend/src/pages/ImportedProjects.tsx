import { projects } from "../api/dummyData";
import { Button, Stack, Typography } from "@mui/material";
import { ImportProjectCardItem } from "../components/ImportProjectCardItem";
import { useState } from "react";
import { SelectFromMapForm } from "../components/SelectFromMapForm";
import { TimelineForm } from "../components/TimelineForm";
import useAllowedActions from "../hooks/useAllowedActions";
import ActionNotAllowed from "./ActionNotAllowed";
import { t } from "i18next";

type Props = {
    type: string;
};
export const ImportedProjects = (props: Props) => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const [createProjectForm, setCreateProjectForm] = useState<any>(null);
    const [selectedProject, setSelectedProject] = useState<Array<number> | []>([]);
    const [overwriteProjectId, setOverwriteProjectId] = useState<Array<{ projectId: number; willBeOverWrittenId: number }> | []>([]);
    const [currentPage, setCurrentPage] = useState(1);
    //ToDo Add data as a prop or get from api which imported from excel
    const importedDummyProjects = projects.filter((a, i) => i < 3);
    //Add default status new for each project
    const projectTypeInitialState = importedDummyProjects.map((p) => {
        return { id: p.id, status: "new" };
    });
    const { allowedActions } = useAllowedActions();
    const [projectsType, setProjectsType] = useState<Array<{ id: number; status: string }>>(projectTypeInitialState);

    if (!allowedActions.includes("IMPORT_PROJECTS")) {
        return <ActionNotAllowed errorMessage={t("exchangeData.importForbidden")} />;
    }
    return (
        <Stack pb={10} direction="column">
            <Typography fontSize="20px" fontWeight="600" sx={{ mt: 2 }}>
                Importeren vanuit {props.type}
            </Typography>
            {currentPage === 1 &&
                importedDummyProjects.map((p) => {
                    return (
                        <ImportProjectCardItem
                            key={p.id}
                            project={p}
                            setSelectedProject={setSelectedProject}
                            selectedProject={selectedProject}
                            overwriteProjectId={overwriteProjectId}
                            setOverwriteProjectId={setOverwriteProjectId}
                            projectsType={projectsType}
                            setProjectsType={setProjectsType}
                        />
                    );
                })}
            {currentPage === 2 && (
                <Stack pb={5}>
                    <SelectFromMapForm setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />
                    <TimelineForm setCreateProjectForm={setCreateProjectForm} createProjectForm={createProjectForm} />
                </Stack>
            )}
            {currentPage === 3 && <Typography sx={{ mt: 4 }}>Gelukt! Je ontvangt de bevestiging via de mail.</Typography>}
            <Stack direction="row" alignItems="center" justifyContent="flex-end">
                {currentPage !== 3 && (
                    <Button variant="contained" disabled={currentPage === 1} onClick={() => setCurrentPage(1)}>
                        Vorig
                    </Button>
                )}
                {currentPage !== 3 && (
                    <Button
                        variant="contained"
                        disabled={selectedProject.length === 0}
                        onClick={() => {
                            if (currentPage === 2) {
                                setCurrentPage(3);
                            } else {
                                if (projectsType.some((item) => item.status === "new")) {
                                    setCurrentPage(2);
                                } else {
                                    setCurrentPage(3);
                                }
                            }
                        }}
                    >
                        Volgende
                    </Button>
                )}
            </Stack>
        </Stack>
    );
};
