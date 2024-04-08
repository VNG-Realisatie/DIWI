import React, { useState } from "react";
import { Chip, Button, TextField, Box, IconButton, InputLabel } from "@mui/material";
import { t } from "i18next";
import { OrdinalCategoryType } from "../../api/adminSettingServices";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";

type Props = {
    ordinalCategoryValue: OrdinalCategoryType[];
    setOrdinalCategoryValue: (value: OrdinalCategoryType[]) => void;
};

export const OrdinalCategoryCreateOption: React.FC<Props> = ({ ordinalCategoryValue, setOrdinalCategoryValue }) => {
    const [newCategory, setNewCategory] = useState("");

    const handleAddCategory = () => {
        if (newCategory.trim() !== "") {
            const updatedCategories = [...ordinalCategoryValue, { name: newCategory, disabled: false, level: ordinalCategoryValue.length }];
            console.log(updatedCategories);
            setOrdinalCategoryValue(updatedCategories);
            setNewCategory("");
        }
    };

    const handleCategoryChange = (index: number, newValue: string) => {
        const updatedCategories = [...ordinalCategoryValue];
        updatedCategories[index] = { name: newValue, disabled: false, level: index };
        setOrdinalCategoryValue(updatedCategories);
    };

    const handleRemoveCategory = (index: number) => {
        const updatedCategories = [...ordinalCategoryValue];
        updatedCategories.splice(index, 1);
        setOrdinalCategoryValue(updatedCategories);
    };

    return (
        <>
            <InputLabel variant="standard" id="categories">
                {t("admin.settings.tableHeader.categories")}
            </InputLabel>
            {ordinalCategoryValue.map((category, index) => (
                <Box key={index} display="flex" alignItems="center" mb={1}>
                    <TextField
                        size="small"
                        fullWidth
                        value={category.name}
                        onChange={(e) => handleCategoryChange(index, e.target.value)}
                        placeholder={t("admin.settings.add")}
                        variant="outlined"
                    />
                    <IconButton aria-label="delete" onClick={() => handleRemoveCategory(index)}>
                        <DeleteIcon />
                    </IconButton>
                </Box>
            ))}
            <Box display="flex" alignItems="center" mb={1}>
                <TextField
                    size="small"
                    fullWidth
                    value={newCategory}
                    onChange={(e) => setNewCategory(e.target.value)}
                    placeholder={t("admin.settings.add")}
                    variant="outlined"
                />
                <IconButton aria-label="add" onClick={handleAddCategory}>
                    <AddIcon />
                </IconButton>
            </Box>
        </>
    );
};
