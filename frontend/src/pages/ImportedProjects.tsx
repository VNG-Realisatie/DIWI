import { projects } from "../api/dummyData";
import { Button, Stack, Typography } from "@mui/material";
import { ImportProjectCardItem } from "../components/ImportProjectCardItem";
import { useState } from "react";

export const ImportedProjects = () => {
    const [selectedProject, setSelectedProject] = useState<Array<number> | []>(
        []
    );
    const [overwriteProjectId, setOverwriteProjectId] = useState<
        Array<{ projectId: number; willBeOverWrittenId: number }> | []
    >([]);
    const [currentPage, setCurrentPage] = useState(1);
    //ToDo Add data as a prop or get from api which imported from excel
    const importedDummyProjects = projects.filter((a, i) => i < 3);
    //Add default status new for each project
    const projectTypeInitialState = importedDummyProjects.map((p) => {
        return { id: p.id, status: "new" };
    });
    const [projectsType, setProjectsType] = useState<
        Array<{ id: number; status: string }>
    >(projectTypeInitialState);
    return (
        <Stack pb={10} direction="column">
            <Typography fontSize="20px" fontWeight="600" sx={{ mt: 2 }}>
                Importeren vanuit Excel
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
            {currentPage === 2 && <>Page 2</>}
            {currentPage === 3 && (
                <Typography sx={{ mt: 4 }}>
                    Gelukt! Je ontvangt de bevestiging via de mail.
                </Typography>
            )}
            <Stack
                direction="row"
                alignItems="center"
                justifyContent="flex-end"
            >
                {currentPage !== 3 && (
                    <Button
                        variant="contained"
                        disabled={currentPage === 1}
                        onClick={() => setCurrentPage(1)}
                    >
                        Vorig
                    </Button>
                )}
                {currentPage !== 3 && (
                    <Button
                        variant="contained"
                        disabled={selectedProject.length === 0}
                        onClick={() =>
                            projectsType.some((item) => item.status === "new")
                                ? setCurrentPage(2)
                                : setCurrentPage(3)
                        }
                    >
                        Volgende
                    </Button>
                )}
            </Stack>
        </Stack>
    );
};
