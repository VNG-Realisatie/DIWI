import { GridRenderCellParams } from "@mui/x-data-grid";
import { Project } from "../../api/projectsServices";
import { MultiSelect } from "./MultiSelect";
import { OptionType, SelectedOptionWithId } from "../ProjectsTableView";
import { useTranslation } from "react-i18next";

type Props = {
    cellValues: GridRenderCellParams<Project>;
    selectedBuurt: SelectedOptionWithId[];
    handleBuurtChange: (_: React.ChangeEvent<{}>, values: OptionType[], id: string) => void;
};
//This will be updated later with endpoint response
const buurtOptions = [
    { id: "Centrum-Noord", title: "Centrum-Noord" },
    { id: "Centrum-Zuid", title: "Centrum-Zuid" },
    { id: "Oranjebuurt", title: "Oranjebuurt" },
    { id: "Kooiweg", title: "Kooiweg" },
    { id: "Noord-End", title: "Noord-End" },
    { id: "Albert’s Hoeve", title: "Albert’s Hoeve" },
    { id: "Beverwijkerstraat", title: "Beverwijkerstraat" },
    { id: "Buitengebied", title: "Buitengebied" },
    { id: "Bakkum-Noord", title: "Bakkum-Noord" },
];

export const BuurtCell = ({ cellValues, selectedBuurt, handleBuurtChange }: Props) => {
    const { t } = useTranslation();

    const defaultPlanTypes = cellValues.row.wijk?.map((c) => ({ id: c, title: c }));
    const findSelected = selectedBuurt?.find((s) => s.id === cellValues.row.projectId);
    const selectedOption = findSelected ? findSelected.option : [];

    return (
        <MultiSelect
            currentRow={cellValues.row}
            selected={selectedOption}
            options={buurtOptions}
            tagLimit={2}
            defaultOptionValues={defaultPlanTypes}
            inputLabel={t("projects.tableColumns.buurt")}
            placeHolder={t("projects.tableColumns.selectBuurt")}
            handleChange={(_: React.ChangeEvent<{}>, values: OptionType[]) => handleBuurtChange(_, values, cellValues.row.projectId)}
            width="300px"
        />
    );
};
