import React, { useState } from "react";
import { TextField, Box, IconButton, InputLabel } from "@mui/material";
import { t } from "i18next";
import { CategoryType, OrdinalCategoryType } from "../../api/adminSettingServices";
import AddIcon from "@mui/icons-material/Add";
import DeleteOutlineOutlinedIcon from "@mui/icons-material/DeleteOutlineOutlined";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";

type Props = {
    categoryValue: (CategoryType | OrdinalCategoryType)[];
    setCategoryValue: (value: (CategoryType | OrdinalCategoryType)[]) => void;
};

export const CategoryCreateOption: React.FC<Props> = ({ categoryValue, setCategoryValue }) => {
    const [newCategory, setNewCategory] = useState<string>("");

    const hasLevels = categoryValue.some((category) => "level" in category);

    const sortCategories = (a: OrdinalCategoryType | CategoryType, b: OrdinalCategoryType | CategoryType) => {
        if (a.disabled !== b.disabled) {
            return a.disabled ? -1 : 1;
        }
        if ("level" in a && "level" in b) {
            return a.level - b.level;
        }
        return 0;
    };

    const handleAddCategory = () => {
        if (newCategory.trim() !== "") {
            let maxLevel = 0;
            if (hasLevels) {
                const ordinalCategories = categoryValue.filter((c): c is OrdinalCategoryType => !c.disabled);
                if (ordinalCategories.length > 0) {
                    maxLevel = Math.max(...ordinalCategories.map((c: OrdinalCategoryType) => c.level));
                }
            }

            let updatedCategories = hasLevels
                ? [...categoryValue, { name: newCategory, disabled: false, level: maxLevel + 1 }]
                : [...categoryValue, { name: newCategory, disabled: false }];

            setCategoryValue(updatedCategories);
            setNewCategory("");
        }
    };

    const handleCategoryChange = (index: number, newValue: string) => {
        let updatedCategories = [...categoryValue];
        let categoriesDisabledAmount = categoryValue.filter((c) => c.disabled === true).length;

        updatedCategories[index + categoriesDisabledAmount].name = newValue;
        setCategoryValue(updatedCategories);
    };

    const handleRemoveCategory = (index: number) => {
        let updatedCategories = [...categoryValue].sort(sortCategories);
        let categoriesDisabledAmount = categoryValue.filter((c) => c.disabled === true).length;
        updatedCategories[index + categoriesDisabledAmount].id
            ? (updatedCategories[index + categoriesDisabledAmount].disabled = true)
            : updatedCategories.splice(index + categoriesDisabledAmount, 1);

        updatedCategories.forEach((category) => {
            if ("level" in category && category.level > index + 1) {
                category.level -= 1;
            }
        });

        setCategoryValue(updatedCategories);
    };

    const handleMoveUp = (index: number) => {
        let updatedCategories = [...categoryValue].filter((c): c is OrdinalCategoryType => "level" in c).sort(sortCategories);
        let categoriesDisabled = [...categoryValue].filter((c) => c.disabled === true);

        updatedCategories[index + categoriesDisabled.length].level--;
        updatedCategories[index - 1 + categoriesDisabled.length].level++;
        setCategoryValue(updatedCategories);
    };

    const handleMoveDown = (index: number) => {
        let categoriesDisabled = [...categoryValue].filter((c) => c.disabled === true);
        let updatedCategories = [...categoryValue].filter((c): c is OrdinalCategoryType => "level" in c).sort(sortCategories);

        updatedCategories[index + categoriesDisabled.length].level++;
        updatedCategories[index + 1 + categoriesDisabled.length].level--;
        setCategoryValue(updatedCategories);
    };

    return (
        <>
            <InputLabel variant="standard" id="categories">
                {t("admin.settings.tableHeader.categories")}
            </InputLabel>
            {categoryValue
                .filter((c) => c.disabled === false)
                .sort(sortCategories)
                .map((category, index) => (
                    <Box key={category.id ?? index} display="flex" alignItems="center" mb={1}>
                        {hasLevels && <InputLabel style={{ marginRight: "8px", overflow: "visible" }}>{index + 1}.</InputLabel>}
                        <TextField
                            size="small"
                            fullWidth
                            value={category.name}
                            onChange={(e) => handleCategoryChange(index, e.target.value)}
                            placeholder={t("admin.settings.add")}
                            variant="outlined"
                        />
                        {hasLevels && (
                            <>
                                <IconButton aria-label="move-up" onClick={() => handleMoveUp(index)} disabled={index === 0}>
                                    <ArrowUpwardIcon />
                                </IconButton>
                                <IconButton
                                    aria-label="move-down"
                                    onClick={() => handleMoveDown(index)}
                                    disabled={index === categoryValue.filter((c) => c.disabled === false).length - 1}
                                >
                                    <ArrowDownwardIcon />
                                </IconButton>
                            </>
                        )}
                        <IconButton aria-label="delete" onClick={() => handleRemoveCategory(index)}>
                            <DeleteOutlineOutlinedIcon color="error" />
                        </IconButton>
                    </Box>
                ))}
            <Box display="flex" alignItems="center" mb={1}>
                {hasLevels && (
                    <InputLabel style={{ marginRight: "8px", overflow: "visible" }}>{categoryValue.filter((c) => c.disabled === false).length + 1}</InputLabel>
                )}
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
