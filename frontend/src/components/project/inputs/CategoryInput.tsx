import InputLabelStack from "./InputLabelStack";
import { Autocomplete, AutocompleteChangeDetails, AutocompleteChangeReason, TextField } from "@mui/material";

type Option = {
    id: string | number;
    name: string;
};

type SetValueFunction = (event: any, value: Option | Option[] | null, reason: AutocompleteChangeReason, details?: AutocompleteChangeDetails<Option>) => void;

type CategoryInputProps = {
    values: Option[] | Option | null;
    setValue: SetValueFunction;
    nullable?: boolean; // Not implemented
    readOnly: boolean;
    mandatory: boolean;
    title?: string;
    options: Option[];
    multiple: boolean;
};

const isOptionEqualToValue = (option: Option, value: Option): boolean => {
    return option.id === value.id;
};

const CategoryInput = ({ values, setValue, readOnly, mandatory, title, options, multiple }: CategoryInputProps) => {
    return (
        <InputLabelStack mandatory={mandatory} title={title || ""}>
            <Autocomplete
                multiple={multiple}
                size="small"
                disabled={readOnly}
                sx={{
                    "& .MuiInputBase-input.Mui-disabled": {
                        backgroundColor: "#0000",
                    },
                }}
                isOptionEqualToValue={isOptionEqualToValue}
                fullWidth
                options={options ?? []}
                getOptionLabel={(option) => option.name ?? ""}
                value={values ?? null}
                filterSelectedOptions
                onChange={setValue}
                renderInput={(params) => <TextField {...params} />}
            />
        </InputLabelStack>
    );
};

export default CategoryInput;
