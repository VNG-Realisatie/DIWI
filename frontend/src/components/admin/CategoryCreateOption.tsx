import React from "react";
import { Autocomplete, Chip, TextField } from "@mui/material";
import { t } from "i18next";

type Props = {
    categoryValue: string[];
    setCategoryValue: (value: string[]) => void;
};

export const CategoryCreateOption: React.FC<Props> = ({ categoryValue, setCategoryValue }) => {
    return (
        <Autocomplete
            multiple
            id="tags-filled"
            options={categoryValue}
            value={categoryValue}
            onChange={(event, newValue) => {
                setCategoryValue(newValue);
            }}
            freeSolo
            renderTags={(value: readonly string[], getTagProps) =>
                value.map((option: string, index: number) => <Chip variant="outlined" label={option} {...getTagProps({ index })} />)
            }
            renderInput={(params) => <TextField {...params} label={t("admin.settings.tableHeader.categories")} placeholder={t("admin.settings.add")} />}
        />
    );
};
