import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../ProjectsTableView";
import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react";
import { ProductTableOption, getMunicipalityList } from "../../api/productTableServices";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedMunicipality: SelectedOptionWithId[];
    handleMunicipalityChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};

export const MunicipalityCell = ({ cellValues, selectedMunicipality, handleMunicipalityChange }: Props) => {
    const [municipalityOptions, setMunicipalityOptions] = useState<ProductTableOption[]>([]);
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.municipality?.map((c) => ({ id: c, name: c }));
    const findSelected = selectedMunicipality?.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];

    useEffect(() => {
        getMunicipalityList().then((municipalities) => setMunicipalityOptions(municipalities));
    }, []);

    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={municipalityOptions}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes}
            inputLabel={t("projects.tableColumns.municipality")}
            placeHolder={t("projects.tableColumns.selecMunicipality")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handleMunicipalityChange(_, values, cellValues.row.projectId)}
            width="300px"
        />
    );
};
