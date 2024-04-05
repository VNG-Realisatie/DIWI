import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { ProjectsChip } from "./ProjectsChip";

type Props = {
    cellValues: GridRenderCellParams<Project>;
};

export const MunicipalityRoleCell = ({ cellValues }: Props) => {
    const defaultPlanTypes = cellValues.row.municipalityRole || [];

    return <ProjectsChip tagLimit={2} values={defaultPlanTypes ? defaultPlanTypes : []} />;
};
