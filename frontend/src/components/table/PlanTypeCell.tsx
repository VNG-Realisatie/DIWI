import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { ProjectsChip } from "./ProjectsChip";
import { t } from "i18next";

type Props = {
    cellValues: GridRenderCellParams<Project>;
};

export const PlanTypeCell = ({ cellValues }: Props) => {
    const defaultPlanTypes = cellValues.row.planType?.map((c) => ({ id: c, name: t(`projectTable.planTypeOptions.${c}`) })) || [];

    return <ProjectsChip tagLimit={2} values={defaultPlanTypes ? defaultPlanTypes : []} />;
};
