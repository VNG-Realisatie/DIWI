import React from "react";
import { Autocomplete, Chip, InputLabel, TextField } from "@mui/material";
import { t } from "i18next";
import { CategoryType } from "../../api/adminSettingServices";

type Props = {
    categoryValue: CategoryType[];
    setCategoryValue: (value: CategoryType[]) => void;
};

export const CategoryCreateOption: React.FC<Props> = ({ categoryValue, setCategoryValue }) => {
    return (
        <>
            <InputLabel variant="standard" id="categories">
                {t("admin.settings.tableHeader.categories")}
            </InputLabel>
            <Autocomplete
                size="small"
                multiple
                id="tags-filled"
                options={categoryValue}
                value={categoryValue}
                onChange={(event, newValue) => {
                    setCategoryValue(
                        newValue.map((value) => {
                            if (typeof value === "string") {
                                return { name: value, disabled: false };
                            } else {
                                return value;
                            }
                        }),
                    );
                }}
                getOptionLabel={(value) => (typeof value === "string" ? value : value.name)}
                freeSolo
                renderTags={(value: CategoryType[], getTagProps) =>
                    value.map((option: CategoryType, index: number) => <Chip variant="outlined" label={option.name} {...getTagProps({ index })} />)
                }
                renderInput={(params) => <TextField {...params} label={t("admin.settings.tableHeader.categories")} placeholder={t("admin.settings.add")} />}
            />
        </>
    );
};
