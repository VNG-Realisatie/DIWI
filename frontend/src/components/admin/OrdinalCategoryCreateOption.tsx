import React, { useState } from "react";
import { TextField, Box, IconButton, InputLabel } from "@mui/material";
import { t } from "i18next";
import { OrdinalCategoryType } from "../../api/adminSettingServices";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";

type Props = {
    ordinalCategoryValue: OrdinalCategoryType[];
    setOrdinalCategoryValue: (value: OrdinalCategoryType[]) => void;
};

export const OrdinalCategoryCreateOption: React.FC<Props> = ({ ordinalCategoryValue, setOrdinalCategoryValue }) => {
    const [newCategory, setNewCategory] = useState("");

    const handleAddCategory = () => {
        if (newCategory.trim() !== "") {
            const maxLevel = ordinalCategoryValue.length > 0 ? ordinalCategoryValue[ordinalCategoryValue.length - 1].level : 0;
            console.log(maxLevel);
            const updatedCategories = [...ordinalCategoryValue, { name: newCategory, disabled: false, level: maxLevel + 1 }];
            console.log(updatedCategories);
            setOrdinalCategoryValue(updatedCategories);
            setNewCategory("");
        }
    };

    const handleCategoryChange = (level: number, newValue: string) => {
        const updatedCategories = [...ordinalCategoryValue];
        updatedCategories[level - 1] = {
            ...updatedCategories[level - 1],
            name: newValue,
        };
        setOrdinalCategoryValue(updatedCategories);
    };

    const handleRemoveCategory = (level: number) => {
        console.log(level);
        const updatedCategories = ordinalCategoryValue.filter((category) => category.level !== level);
        console.log(updatedCategories);
        updatedCategories.forEach((category) => {
            if (category.level > level) {
                category.level -= 1;
            }
        });
        setOrdinalCategoryValue(updatedCategories);
    };

    const handleMoveUp = (level: number) => {
        if (level > 1) {
            const updatedCategories = [...ordinalCategoryValue];
            updatedCategories[level - 1].level--;
            updatedCategories[level - 2].level++;
            setOrdinalCategoryValue(updatedCategories.sort((a, b) => a.level - b.level));
        }
    };

    const handleMoveDown = (level: number) => {
        if (level < ordinalCategoryValue.length) {
            const updatedCategories = [...ordinalCategoryValue];
            updatedCategories[level - 1].level++;
            updatedCategories[level].level--;
            setOrdinalCategoryValue(updatedCategories.sort((a, b) => a.level - b.level));
        }
    };

    return (
        <>
            <InputLabel variant="standard" id="categories">
                {t("admin.settings.tableHeader.categories")}
            </InputLabel>
            {ordinalCategoryValue
                .sort((a, b) => a.level - b.level)
                .map((category) => (
                    <Box key={category.level} display="flex" alignItems="center" mb={1}>
                        <InputLabel style={{ marginRight: "8px", overflow: "visible" }}>{category.level}</InputLabel>
                        <TextField
                            size="small"
                            fullWidth
                            value={category.name}
                            onChange={(e) => handleCategoryChange(category.level, e.target.value)}
                            placeholder={t("admin.settings.add")}
                            variant="outlined"
                        />
                        <IconButton aria-label="move-up" onClick={() => handleMoveUp(category.level)} disabled={category.level === 1}>
                            <ArrowUpwardIcon />
                        </IconButton>
                        <IconButton
                            aria-label="move-down"
                            onClick={() => handleMoveDown(category.level)}
                            disabled={category.level === ordinalCategoryValue.length}
                        >
                            <ArrowDownwardIcon />
                        </IconButton>
                        <IconButton aria-label="delete" onClick={() => handleRemoveCategory(category.level)}>
                            <DeleteIcon />
                        </IconButton>
                    </Box>
                ))}
            <Box display="flex" alignItems="center" mb={1}>
                <InputLabel style={{ marginRight: "8px", overflow: "visible" }}>{ordinalCategoryValue.length + 1}</InputLabel>
                <TextField
                    size="small"
                    fullWidth
                    value={newCategory}
                    onChange={(e) => setNewCategory(e.target.value)}
                    placeholder={t("admin.settings.add")}
                    variant="outlined"
                />
            </Box>
            <Box display="flex" alignItems="center" mb={1}>
                <IconButton aria-label="add" onClick={handleAddCategory}>
                    <AddIcon />
                </IconButton>
            </Box>
        </>
    );
};
