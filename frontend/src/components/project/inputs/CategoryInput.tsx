import InputLabelStack from "./InputLabelStack";
import { Autocomplete, AutocompleteChangeDetails, AutocompleteChangeReason, Chip, TextField } from "@mui/material";
import { t } from "i18next";
import { TooltipInfo } from "../../../widgets/TooltipInfo";

type Option = {
    id: string | number;
    name: string;
};

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
    tooltipInfoText?: string;
};
const isOptionEqualToValue = (option: Option, value: Option): boolean => {
    return option.id === value.id;
};

const getErrorHelperText = (mandatory: boolean, readOnly: boolean, values: any, error?: string) => {
    const hasError = mandatory && !values && !readOnly;
    const helperText = hasError ? error : "";
    return { hasError, helperText };
};

const CategoryInput = ({
    values,
    setValue,
    readOnly,
    mandatory,
    title,
    options,
    multiple,
    error,
    translationPath = "",
    tooltipInfoText,
}: CategoryInputProps) => {
    const { hasError, helperText } = getErrorHelperText(mandatory, readOnly, values, error);
    return (
        <InputLabelStack mandatory={mandatory} title={title || ""} tooltipInfoText={tooltipInfoText}>
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
                    if (option) {
                        return t(`${translationPath}${option}`);
                    }
                    return "";
                }}
                renderOption={(props, option) =>
                    tooltipInfoText ? (
                        <li {...props}>
                            {t(`${translationPath}${option.name}`)}
                            {<TooltipInfo text={t(`${tooltipInfoText}${option.name}`)} />}
                        </li>
                    ) : (
                        <li {...props}>{t(`${translationPath}${option.name}`)}</li>
                    )
                }
                renderTags={(tagValue, getTagProps) =>
                    tagValue.map((option, index) =>
                        tooltipInfoText ? (
                            <Chip
                                {...getTagProps({ index })}
                                key={option.id}
                                label={<TooltipInfo text={t(`${tooltipInfoText}${option.name}`)}>{t(`${translationPath}${option.name}`)}</TooltipInfo>}
                            />
                        ) : (
                            <Chip {...getTagProps({ index })} key={option.id} label={t(`${translationPath}${option.name}`)} />
                        ),
                    )
                }
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
