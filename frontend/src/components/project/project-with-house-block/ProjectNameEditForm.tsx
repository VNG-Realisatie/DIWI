import { Stack, TextField } from "@mui/material";
import { ChangeEvent, useContext } from "react";
import { useTranslation } from "react-i18next";
import ProjectContext from "../../../context/ProjectContext";

type Props = {
    name?: string;
    setName: (name: string) => void;
};
export const ProjectNameEditForm = ({ name, setName }: Props) => {
    const { t } = useTranslation();

    const { selectedProject } = useContext(ProjectContext);

    const handleNameChange = (event: ChangeEvent<HTMLInputElement>) => {
        setName(event.target.value);
    };

    return (
        <Stack direction="row" alignItems="center" spacing={1}>
            <TextField
                size="small"
                sx={{ border: "solid 1px white" }}
                label={t("projects.tableColumns.projectName")}
                value={name ? name : selectedProject?.projectName}
                onChange={handleNameChange}
            />
        </Stack>
    );
};
