import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { ProjectsChip } from "./ProjectsChip";

type Props = {
    cellValues: GridRenderCellParams<Project>;
};

export const PlanningPlanStatusCell = ({ cellValues }: Props) => {
    const defaultPlanTypes = cellValues.row.planningPlanStatus?.map((c) => ({ id: c, name: c })) || [];

    return <ProjectsChip tagLimit={2} values={defaultPlanTypes ? defaultPlanTypes : []} />;
};
