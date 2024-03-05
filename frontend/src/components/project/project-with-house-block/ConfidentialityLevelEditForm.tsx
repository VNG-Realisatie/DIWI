import { MenuItem, Select, SelectChangeEvent } from "@mui/material";
import { confidentialityLevelOptions } from "../../table/constants";
import { useTranslation } from "react-i18next";
import { useContext } from "react";
import ProjectContext from "../../../context/ProjectContext";

type Props = {
    confidentialityLevel: string | undefined;
    setConfidentialityLevel: (c: string | undefined) => void;
};
export const ConfidentialityLevelEditForm = ({ confidentialityLevel, setConfidentialityLevel }: Props) => {
    const { t } = useTranslation();
    const { selectedProject } = useContext(ProjectContext);

    const handleConfidentialityLevelChange = (event: SelectChangeEvent) => {
        setConfidentialityLevel(event.target.value as string);
    };

    const confidentialityLevelInputValue = () => {
        if (confidentialityLevel) {
            return confidentialityLevel;
        } else if (selectedProject) {
            if (selectedProject.confidentialityLevel !== null && selectedProject.confidentialityLevel !== undefined) {
                return selectedProject.confidentialityLevel;
            }
        }
    };

    return (
        <Select fullWidth size="small" id="confidentiality-level-select" value={confidentialityLevelInputValue()} onChange={handleConfidentialityLevelChange}>
            {confidentialityLevelOptions.map((ppo) => {
                return (
                    <MenuItem key={ppo.id} value={ppo.id}>
                        {t(`projectTable.confidentialityLevelOptions.${ppo.name}`)}
                    </MenuItem>
                );
            })}
        </Select>
    );
};
