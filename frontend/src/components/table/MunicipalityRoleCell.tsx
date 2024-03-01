import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../project/ProjectsTableView";
import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react";
import { getMunicipalityRoleList } from "../../api/projectsTableServices";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedMunicipalityRole: SelectedOptionWithId[];
    handleMunicipalityRoleChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};

export const MunicipalityRoleCell = ({ cellValues, selectedMunicipalityRole, handleMunicipalityRoleChange }: Props) => {
    const [municipalityRolesOptions, setMunicipalityRolesOptions] = useState<OptionType[]>();
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.municipalityRole || [];
    const findSelected = selectedMunicipalityRole.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];

    useEffect(() => {
        getMunicipalityRoleList().then((roles) => setMunicipalityRolesOptions(roles));
    }, []);

    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={municipalityRolesOptions ? municipalityRolesOptions : []}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes}
            inputLabel={t("projects.tableColumns.municipalityRole")}
            placeHolder={t("projects.tableColumns.selectMunicipalityRole")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handleMunicipalityRoleChange(_, values, cellValues.row.projectId)}
            width="300px"
        />
    );
};
