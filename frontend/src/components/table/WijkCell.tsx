import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../project/ProjectsTableView";
import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react";
import { ProductTableOption, getWijkList } from "../../api/projectsTableServices";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedWijk: SelectedOptionWithId[];
    handleWijkChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};

export const WijkCell = ({ cellValues, selectedWijk, handleWijkChange }: Props) => {
    const [wijkOptions, setWijkOptions] = useState<ProductTableOption[]>([]);
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.wijk || [];
    const findSelected = selectedWijk?.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];

    useEffect(() => {
        getWijkList().then((wijks) => setWijkOptions(wijks));
    }, []);

    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={wijkOptions}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes}
            inputLabel={t("projects.tableColumns.wijk")}
            placeHolder={t("projects.tableColumns.selectWijk")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handleWijkChange(_, values, cellValues.row?.projectId)}
            width="300px"
        />
    );
};
