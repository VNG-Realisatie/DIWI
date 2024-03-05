import { MenuItem, Select, SelectChangeEvent } from "@mui/material";
import { projectPhaseOptions } from "../../table/constants";
import { useContext } from "react";
import ProjectContext from "../../../context/ProjectContext";
import { useTranslation } from "react-i18next";

type Props = {
    projectPhase: string | undefined;
    setProjectPhase: (phase: string) => void;
};
export const PhaseEditForm = ({ projectPhase, setProjectPhase }: Props) => {
    const { selectedProject } = useContext(ProjectContext);
    const { t } = useTranslation();

    const handleProjectPhaseChange = (event: SelectChangeEvent<typeof projectPhase>) => {
        if (event.target.value) {
            setProjectPhase(event.target.value);
        }
    };

    const projectPhaseInputValue = () => {
        if (projectPhase) {
            return projectPhase;
        } else if (selectedProject) {
            if (selectedProject.projectPhase !== null && selectedProject.projectPhase !== undefined) {
                return selectedProject.projectPhase;
            }
        }
    };

    return (
        <Select fullWidth size="small" id="project-phase-select" value={projectPhaseInputValue()} onChange={handleProjectPhaseChange}>
            {projectPhaseOptions.map((ppo) => {
                return (
                    <MenuItem key={ppo.id} value={ppo.id}>
                        {t(`projectTable.projectPhaseOptions.${ppo.name}`)}
                    </MenuItem>
                );
            })}
        </Select>
    );
};
