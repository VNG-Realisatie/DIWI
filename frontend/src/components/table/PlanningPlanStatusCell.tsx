import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../ProjectsTableView";
import { useTranslation } from "react-i18next";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedPlanStatus: SelectedOptionWithId[];
    handleStatusChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};
//This will be updated later with endpoint response
const planningPlanStatus = [
    { id: "_1A_ONHERROEPELIJK", name: "_1A_ONHERROEPELIJK" },
    { id: "_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG", name: "_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG" },
    { id: "_1C_ONHERROEPELIJK_MET_BW_NODIG", name: "_1C_ONHERROEPELIJK_MET_BW_NODIG" },
    { id: "_2A_VASTGESTELD", name: "_2A_VASTGESTELD" },
    { id: "_2B_VASTGESTELD_MET_UITWERKING_NODIG", name: "_2B_VASTGESTELD_MET_UITWERKING_NODIG" },
    { id: "_2C_VASTGESTELD_MET_BW_NODIG", name: "_2C_VASTGESTELD_MET_BW_NODIG" },
    { id: "_3_IN_VOORBEREIDING", name: "_3_IN_VOORBEREIDING" },
    { id: "_4A_OPGENOMEN_IN_VISIE", name: "_4A_OPGENOMEN_IN_VISIE" },
    { id: "_4B_NIET_OPGENOMEN_IN_VISIE", name: "_4B_NIET_OPGENOMEN_IN_VISIE" },
];

export const PlanningPlanStatusCell = ({ cellValues, selectedPlanStatus, handleStatusChange }: Props) => {
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.planningPlanStatus.map((c) => ({ id: c, name: c }));
    const findSelected = selectedPlanStatus.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];

    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={planningPlanStatus}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes}
            inputLabel={t("projects.tableColumns.planningPlanStatus")}
            placeHolder={t("projects.tableColumns.selectPlanningPlanStatus")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handleStatusChange(_, values, cellValues.row.projectId)}
            width="500px"
        />
    );
};
