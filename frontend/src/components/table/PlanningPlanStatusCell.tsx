import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../project/ProjectsTableView";
import { useTranslation } from "react-i18next";
import { planningPlanStatus } from "./constants";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedPlanStatus: SelectedOptionWithId[];
    handleStatusChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};

export const PlanningPlanStatusCell = ({ cellValues, selectedPlanStatus, handleStatusChange }: Props) => {
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.planningPlanStatus.map((c) => ({ id: c, name: c }));
    const findSelected = selectedPlanStatus.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];
    const translatedOption = planningPlanStatus.map((p) => {
        return { id: p.id, name: t(`projectTable.planningPlanStatus.${p.name}`) };
    });

    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={translatedOption}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes}
            inputLabel={t("projects.tableColumns.planningPlanStatus")}
            placeHolder={t("projects.tableColumns.selectPlanningPlanStatus")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handleStatusChange(_, values, cellValues.row.projectId)}
            width="500px"
        />
    );
};
