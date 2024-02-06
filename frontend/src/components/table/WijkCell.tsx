import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../ProjectsTableView";
import { useTranslation } from "react-i18next";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedWijk: SelectedOptionWithId[];
    handleWijkChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};
//This will be updated later with endpoint response
const wijkOptions=[
    {id:"Centrum",title:"Centrum"},
    {id:"Castricum-Noord",title:"Castricum-Noord"},
    {id:"Castricum-Oost",title:"Castricum-Oost"},
    {id:"Castricum-Zuid",title:"Castricum-Zuid"},
    {id:"Bakkum",title:"Bakkum"},
    {id:"Akersloot",title:"Akersloot"},
    {id:"De Woude",title:"De Woude"},
    {id:"Limmen",title:"Limmen"}
]

export const WijkCell = ({ cellValues, selectedWijk, handleWijkChange }: Props) => {
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.wijk?.map((c) => ({ id: c, title: c }));
    const findSelected = selectedWijk?.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];

    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={wijkOptions}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes}
            inputLabel={t("projects.tableColumns.wijk")}
            placeHolder={t("projects.tableColumns.selectWijk")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handleWijkChange(_, values, cellValues.row.projectId)}
            width="300px"
        />
    );
};
