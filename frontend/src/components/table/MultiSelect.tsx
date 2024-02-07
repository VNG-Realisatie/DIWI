import { Autocomplete, Stack, TextField } from "@mui/material";
import { Project } from "../../api/projectsServices";
import { useTranslation } from "react-i18next";
type OptionType = {
    id: string;
    name: string;
};
type Props = {
    tagLimit: number;
    options: OptionType[];
    selected: OptionType[];
    defaultOptionValues: OptionType[];
    inputLabel: string;
    placeHolder: string;
    handleChange: (_: React.ChangeEvent<{}>, values: OptionType[]) => void;
    width: string;
    currentRow: Project;
    needTranslation?: boolean;
    selectOptionType?: "planningPlanStatus" | "planTypeOptions" | "municipalityRolesOptions";
};
export const MultiSelect = ({
    tagLimit,
    options,
    selected,
    selectOptionType,
    defaultOptionValues,
    inputLabel,
    placeHolder,
    handleChange,
    width,
    currentRow,
    needTranslation,
}: Props) => {
    const { t } = useTranslation();

    return (
        <Stack direction="row" spacing={1}>
            <Autocomplete
                size="small"
                multiple
                limitTags={tagLimit}
                id="multiple-limit-tags"
                options={options ? options : []}
                getOptionLabel={(option) => (needTranslation ? t(`projectTable.${selectOptionType}.${option.name}`) : option.name)}
                isOptionEqualToValue={(option, value) => option.id === value.id}
                value={selected.length > 1 ? selected : defaultOptionValues}
                renderInput={(params) => <TextField {...params} label={inputLabel} placeholder={placeHolder} />}
                sx={{ width }}
                onChange={handleChange}
            />
        </Stack>
    );
};
