import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../ProjectsTableView";
import { useTranslation } from "react-i18next";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedMunicipality: SelectedOptionWithId[];
    handleMunicipalityChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};

//This will be updated later with endpoint response
const municipalityRolesOptions: OptionType[] = [
    { id: "ACTIVE", title: "ACTIVE" },
    { id: "PASSIVE", title: "PASSIVE" },
    { id: "NOTHING", title: "NOTHING" },
];

export const MunicipalityRoleCell = ({ cellValues, selectedMunicipality, handleMunicipalityChange }: Props) => {
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.municipalityRole.map((c) => ({ id: c, title: c }));
    const findSelected = selectedMunicipality.find((s) => s.id === cellValues.row.projectId);
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
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handleMunicipalityChange(_, values, cellValues.row.projectId)}
            width="300px"
        />
    );
};
