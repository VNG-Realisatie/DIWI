import InputLabelStack from "./InputLabelStack";
import { Autocomplete, AutocompleteChangeDetails, AutocompleteChangeReason, TextField } from "@mui/material";

type Option = {
    id: string | number;
    name: string;
};

type SetValueFunction = (event: any, value: any, reason: AutocompleteChangeReason, details?: AutocompleteChangeDetails<Option>) => void;

type CategoryInputProps = {
    values: any;
    setValue: SetValueFunction;
    nullable?: boolean; // Not implemented
    readOnly: boolean;
    mandatory: boolean;
    title?: string;
    options: Option[];
    multiple: boolean;
    error?: string;
};

const formatString = (str: string) => {
    const formattedString = str.replace(/_/g, " ").trimStart();
    return formattedString.charAt(0).toUpperCase() + formattedString.slice(1).toLowerCase();
};

const formatOptions = (options: any) => {
    return options.map((option: any) => ({
        ...option,
        name: formatString(option.name),
    }));
};

const isOptionEqualToValue = (option: Option, value: Option): boolean => {
    return option.id === value.id;
};

const getErrorHelperText = (mandatory: boolean, readOnly: boolean, values: any, error?: string) => {
    const hasError = mandatory && !values && !readOnly;
    const helperText = hasError ? error : "";
    return { hasError, helperText };
};

const CategoryInput = ({ values, setValue, readOnly, mandatory, title, options, multiple, error }: CategoryInputProps) => {
    const { hasError, helperText } = getErrorHelperText(mandatory, readOnly, values, error);
    const formattedOptions = formatOptions(options);
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
                options={formattedOptions ?? []}
                getOptionLabel={(option) => formatString(option.name) ?? ""}
                value={values ?? null}
                filterSelectedOptions
                onChange={setValue}
                renderInput={(params) => (
                    <>
                        <TextField {...params} variant="outlined" error={hasError} helperText={helperText} />
                    </>
                )}
            />
        </InputLabelStack>
    );
};

export default CategoryInput;
