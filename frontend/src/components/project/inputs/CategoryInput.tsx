import InputLabelStack from "./InputLabelStack";
import { Autocomplete, AutocompleteChangeDetails, AutocompleteChangeReason, Chip, TextField } from "@mui/material";
import { t } from "i18next";
import { TooltipInfo } from "../../../widgets/TooltipInfo";

type Option = {
    uuid?: string | number;
    id?: string | number;
    name?: string;
    firstName?: string;
    lastName?: string;
};

type SetValueFunction = (
    event: React.SyntheticEvent,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    value: any,
    reason: AutocompleteChangeReason,
    details?: AutocompleteChangeDetails<Option>,
) => void;
type CategoryInputProps = {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    values: any;
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
    hasTooltipOption?: boolean;
    displayError?: boolean;
};
const isOptionEqualToValue = (option: Option, value: Option): boolean => {
    return option.id === value.id;
};

const getErrorHelperText = (mandatory: boolean, readOnly: boolean, values: Option | Option[] | null, displayError: boolean, error?: string) => {
    const hasError = (mandatory && (!values || (Array.isArray(values) && values.length === 0)) && !readOnly) || displayError;
    const helperText = hasError ? error : "";
    return { hasError, helperText };
};
const getDisplayName = (option: Option, translationPath: string) =>
    option.firstName && option.lastName ? `${option.firstName} ${option.lastName}` : `${translationPath}${option.name}`;

const getTooltipText = (hasTooltipOption: boolean, tooltipInfoText: string, optionName: string) =>
    hasTooltipOption ? `${tooltipInfoText}${optionName}`.replace("title", "") : "";

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
    hasTooltipOption = false,
    displayError = false,
}: CategoryInputProps) => {
    const { hasError, helperText } = getErrorHelperText(mandatory, readOnly, values, displayError, error);

    const autocompleteComponent = (
        <Autocomplete
            multiple={multiple}
            size="small"
            disabled={readOnly}
            isOptionEqualToValue={isOptionEqualToValue}
            fullWidth
            options={options ?? []}
            getOptionLabel={(option) => {
                if (option && option.firstName && option.lastName) {
                    return `${option.firstName} ${option.lastName}`;
                }
                if (option && option.name) {
                    return t(`${translationPath}${option.name}`);
                }
                if (option) {
                    return t(`${translationPath}${option}`);
                }
                return "";
            }}
            filterOptions={(options) => {
                if (multiple && values) {
                    const selectedIds = Array.isArray(values) ? values.map((value) => value.uuid) : [values.uuid];
                    return options.filter((option) => !selectedIds.includes(option.id));
                }
                return options;
            }}
            renderOption={(props, option) => {
                const displayName = getDisplayName(option, translationPath);
                const tooltipText = getTooltipText(hasTooltipOption, tooltipInfoText || "", option.name || "");

                return (
                    <li {...props}>
                        {t(displayName)}
                        {hasTooltipOption && <TooltipInfo text={t(tooltipText)} />}
                    </li>
                );
            }}
            renderTags={(tagValue, getTagProps) =>
                tagValue.map((option, index) => {
                    const displayName = getDisplayName(option, translationPath);
                    const tooltipText = getTooltipText(hasTooltipOption, tooltipInfoText || "", option.name || "");

                    return (
                        <Chip
                            {...getTagProps({ index })}
                            disabled={readOnly}
                            key={option.id}
                            label={hasTooltipOption ? <TooltipInfo text={t(tooltipText)}>{t(displayName)}</TooltipInfo> : t(displayName)}
                        />
                    );
                })
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
    );

    return title ? (
        <InputLabelStack mandatory={mandatory} title={title} tooltipInfoText={tooltipInfoText}>
            {autocompleteComponent}
        </InputLabelStack>
    ) : (
        autocompleteComponent
    );
};

export default CategoryInput;
