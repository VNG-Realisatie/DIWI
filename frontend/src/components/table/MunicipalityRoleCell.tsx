import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../ProjectsTableView";
import { useTranslation } from "react-i18next";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedMunicipalityRole: SelectedOptionWithId[];
    handleMunicipalityRoleChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};

//This will be updated later with endpoint response
const municipalityRolesOptions: OptionType[] = [
    { id: "ACTIVE", name: "ACTIVE" },
    { id: "PASSIVE", name: "PASSIVE" },
    { id: "NOTHING", name: "NOTHING" },
];

export const MunicipalityRoleCell = ({ cellValues, selectedMunicipalityRole, handleMunicipalityRoleChange }: Props) => {
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.municipalityRole.map((c) => ({ id: c, name: c }));
    const findSelected = selectedMunicipalityRole.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];

    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={municipalityRolesOptions}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes}
            inputLabel={t("projects.tableColumns.municipalityRole")}
            placeHolder={t("projects.tableColumns.selectMunicipalityRole")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handleMunicipalityRoleChange(_, values, cellValues.row.projectId)}
            width="300px"
        />
    );
};
