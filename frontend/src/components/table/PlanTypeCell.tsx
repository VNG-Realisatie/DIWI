import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../ProjectsTableView";
import { useTranslation } from "react-i18next";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedPlanTypes: SelectedOptionWithId[];
    handlePlanTypeChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};

const planTypeOptions: OptionType[] = [
    { id: "PAND_TRANSFORMATIE", title: "PAND_TRANSFORMATIE" },
    { id: "TRANSFORMATIEGEBIED", title: "TRANSFORMATIEGEBIED" },
    { id: "HERSTRUCTURERING", title: "HERSTRUCTURERING" },
    { id: "VERDICHTING", title: "VERDICHTING" },
    { id: "UITBREIDING_UITLEG", title: "UITBREIDING_UITLEG" },
    { id: "UITBREIDING_OVERIG", title: "UITBREIDING_OVERIG" },
];

export const PlanTypeCell = ({ cellValues, selectedPlanTypes, handlePlanTypeChange }: Props) => {
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.planType.map((c) => ({ id: c, title: c }));
    const findSelected = selectedPlanTypes.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];

    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={planTypeOptions}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes}
            inputLabel={t("projects.tableColumns.planType")}
            placeHolder={t("projects.tableColumns.selectPlanType")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handlePlanTypeChange(_, values, cellValues.row.projectId)}
            width="500px"
        />
    );
};
