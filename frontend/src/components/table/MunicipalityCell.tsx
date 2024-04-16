import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { ProjectsChip } from "./ProjectsChip";

type Props = {
    cellValues: GridRenderCellParams<Project>;
};

export const MunicipalityCell = ({ cellValues }: Props) => {
    const defaultPlanTypes = cellValues.row.municipality || [];

    return <ProjectsChip tagLimit={2} values={defaultPlanTypes ? defaultPlanTypes : []} />;
};
