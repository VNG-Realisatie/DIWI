import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../project/ProjectsTableView";
import { useTranslation } from "react-i18next";
import { ProjectTableOption, getNeighbourhoodList } from "../../api/projectsTableServices";
import { useEffect, useState } from "react";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedNeighbourhood: SelectedOptionWithId[];
    handleNeighbourhoodChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};

export const BuurtCell = ({ cellValues, selectedNeighbourhood, handleNeighbourhoodChange }: Props) => {
    const [buurtOptions, setBuurtOptions] = useState<ProjectTableOption[]>([]);

    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.wijk || [];
    const findSelected = selectedNeighbourhood?.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];

    useEffect(() => {
        getNeighbourhoodList().then((neighbourhoods) => setBuurtOptions(neighbourhoods));
    }, []);
    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={buurtOptions}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes ? defaultPlanTypes : []}
            inputLabel={t("projects.tableColumns.neighbourhood")}
            placeHolder={t("projects.tableColumns.selectNeighbourhood")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) =>
                cellValues.row.projectId && handleNeighbourhoodChange(_, values, cellValues.row.projectId)
            }
            width="300px"
        />
    );
};
