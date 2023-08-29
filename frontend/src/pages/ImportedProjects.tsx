import { projects } from "../api/dummyData";
import { Typography } from "@mui/material";
import { ImportProjectCardItem } from "../components/ImportProjectCardItem";
import { useState } from "react";

export const ImportedProjects = () => {
    const [selectedProject, setSelectedProject] = useState<Array<number> | []>(
        []
    );
    //ToDo Add data as a prop or get from api which imported from excel
    const importedDummyProjects = projects.filter((a, i) => i < 3);
    return (
        <>
            <Typography fontSize="20px" fontWeight="600" sx={{ mt: 2 }}>
                Importeren vanuit Excel
            </Typography>
            {importedDummyProjects.map((p) => {
                return (
                    <ImportProjectCardItem
                        key={p.id}
                        project={p}
                        setSelectedProject={setSelectedProject}
                        selectedProject={selectedProject}
                    />
                );
            })}
        </>
    );
};
