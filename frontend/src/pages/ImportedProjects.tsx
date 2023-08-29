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
    console.log(overwriteProjectId)
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
                        />
                    );
                })}
            {currentPage === 2 && <>Page 2</>}
            <Stack
                direction="row"
                alignItems="center"
                justifyContent="flex-end"
            >
                <Button
                    variant="contained"
                    disabled={currentPage === 1}
                    onClick={() => setCurrentPage(1)}
                >
                    Vorig
                </Button>
                <Button
                    variant="contained"
                    disabled={selectedProject.length === 0}
                    onClick={() => setCurrentPage(2)}
                >
                    Volgende
                </Button>
            </Stack>
        </Stack>
    );
};
