import InputLabelStack from "./InputLabelStack";
import { Autocomplete, Chip, ListItemText, MenuItem, OutlinedInput, Select, TextField } from "@mui/material";
import { MenuProps } from "../../../utils/menuProps";
import { t } from "i18next";

type Props = {
    values: any;
    setValue: any;
    nullable?: boolean; //not implemented
    readOnly: boolean;
    mandatory: boolean;
    error?: string | undefined | null;
    title?: string;
    errorText?: string;
    options: Array<any>;
    translationPath?: string;
    multiple: boolean;
};

const getDateErrorMessage = (error: string | undefined | null, value: string | null, errorText: string): string | null => {
    if (error) {
        return error;
    }
    if (!value) {
        return errorText;
    }
    return null;
};
const EnumInput = ({ values, setValue, readOnly, mandatory, error, title, errorText, options, translationPath, multiple }: Props) => {
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

export default EnumInput;
