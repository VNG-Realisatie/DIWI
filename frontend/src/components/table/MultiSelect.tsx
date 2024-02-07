import { Autocomplete, Stack, TextField } from "@mui/material";
import { Project } from "../../api/projectsServices";
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
};
export const MultiSelect = ({ tagLimit, options, selected, defaultOptionValues, inputLabel, placeHolder, handleChange, width, currentRow }: Props) => {
    return (
        <Stack direction="row" spacing={1}>
            <Autocomplete
                size="small"
                multiple
                limitTags={tagLimit}
                id="multiple-limit-tags"
                options={options?options:[]}
                getOptionLabel={(option) => option.name}
                isOptionEqualToValue={(option, value) => option.id === value.id}
                value={selected.length > 1 ? selected : defaultOptionValues}
                renderInput={(params) => <TextField {...params} label={inputLabel} placeholder={placeHolder} />}
                sx={{ width }}
                onChange={handleChange}
            />
        </Stack>
    );
};
