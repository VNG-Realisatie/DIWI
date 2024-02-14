import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../ProjectsTableView";
import { useTranslation } from "react-i18next";
import { planTypeOptions } from "./constants";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedPlanTypes: SelectedOptionWithId[];
    handlePlanTypeChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};

export const PlanTypeCell = ({ cellValues, selectedPlanTypes, handlePlanTypeChange }: Props) => {
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.planType.map((c) => ({ id: c, name: c }));
    const findSelected = selectedPlanTypes.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];
    const translatedOption = planTypeOptions.map((p) => {
        return { id: p.id, name: t(`projectTable.planTypeOptions.${p.name}`) };
    });

    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={translatedOption}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes}
            inputLabel={t("projects.tableColumns.planType")}
            placeHolder={t("projects.tableColumns.selectPlanType")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handlePlanTypeChange(_, values, cellValues.row.projectId)}
            width="500px"
        />
    );
};
