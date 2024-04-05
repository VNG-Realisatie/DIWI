import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { ProjectsChip } from "./ProjectsChip";

type Props = {
    cellValues: GridRenderCellParams<Project>;
};

export const BuurtCell = ({ cellValues }: Props) => {
    const defaultPlanTypes = cellValues.row.wijk || [];

    return <ProjectsChip tagLimit={2} values={defaultPlanTypes ? defaultPlanTypes : []} />;
};
