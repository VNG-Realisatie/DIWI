import InputLabelStack from "./InputLabelStack";
import { Autocomplete, AutocompleteChangeDetails, AutocompleteChangeReason, TextField } from "@mui/material";
import { t } from "i18next";

type Option = {
    id: string | number;
    name: string;
    firstName?: string;
    lastName?: string;
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
type SetValueFunction = (event: any, value: any, reason: AutocompleteChangeReason, details?: AutocompleteChangeDetails<Option>) => void;

type CategoryInputProps = {
    values: Option | Option[] | null;
    setValue: SetValueFunction;
    nullable?: boolean; // Not implemented
    readOnly: boolean;
    mandatory: boolean;
    title?: string;
    options: Option[];
    multiple: boolean;
    error?: string;
    translationPath?: string;
};
const isOptionEqualToValue = (option: Option, value: Option): boolean => {
    return option.id === value.id;
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const getErrorHelperText = (mandatory: boolean, readOnly: boolean, values: any, error?: string) => {
    const hasError = mandatory && (!values || values.length === 0) && !readOnly;
    const helperText = hasError ? error : "";
    return { hasError, helperText };
};

const CategoryInput = ({ values, setValue, readOnly, mandatory, title, options, multiple, error, translationPath = "" }: CategoryInputProps) => {
    const { hasError, helperText } = getErrorHelperText(mandatory, readOnly, values, error);
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
                getOptionLabel={(option) => {
                    if (option && option.name) {
                        return t(`${translationPath}${option.name}`);
                    }
                    if (option && option.firstName && option.lastName) {
                        return `${option.firstName}${option.lastName}`;
                    }
                    if (option) {
                        return t(`${translationPath}${option}`);
                    }
                    return "";
                }}
                value={values}
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
