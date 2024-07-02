import React from "react";
import { TextField, Box, IconButton, InputLabel, Stack } from "@mui/material";
import { t } from "i18next";
import { CategoryType, OrdinalCategoryType } from "../../api/adminSettingServices";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import DeleteOutlineOutlinedIcon from "@mui/icons-material/DeleteOutlineOutlined";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";

type Props = {
    categoryValue: (CategoryType | OrdinalCategoryType)[];
    setCategoryValue: (value: (CategoryType | OrdinalCategoryType)[]) => void;
    ordered?: boolean;
};

export const CategoryCreateOption: React.FC<Props> = ({ categoryValue, setCategoryValue, ordered }) => {
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
        let maxLevel = 0;
        if (ordered) {
            const ordinalCategories = categoryValue.filter((c) => !c.disabled);
            if (ordinalCategories.length > 0) {
                maxLevel = Math.max(...ordinalCategories.filter((c): c is OrdinalCategoryType => "level" in c).map((c) => c.level));
            }
        }

        const updatedCategories = ordered
            ? [...categoryValue, { name: "", disabled: false, level: maxLevel + 1 }]
            : [...categoryValue, { name: "", disabled: false }];

        setCategoryValue(updatedCategories.sort(sortCategories));
    };

    const handleCategoryChange = (index: number, newValue: string) => {
        const updatedCategories = [...categoryValue];
        const categoriesDisabledAmount = categoryValue.filter((c) => c.disabled === true).length;

        updatedCategories[index + categoriesDisabledAmount].name = newValue;
        setCategoryValue(updatedCategories);
    };

    const handleRemoveCategory = (index: number) => {
        const updatedCategories = [...categoryValue].sort(sortCategories);
        const categoriesDisabledAmount = categoryValue.filter((c) => c.disabled === true).length;
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
        const updatedCategories = [...categoryValue].filter((c): c is OrdinalCategoryType => "level" in c).sort(sortCategories);
        const categoriesDisabled = [...categoryValue].filter((c) => c.disabled === true);

        updatedCategories[index + categoriesDisabled.length].level--;
        updatedCategories[index - 1 + categoriesDisabled.length].level++;
        setCategoryValue(updatedCategories.sort(sortCategories));
    };

    const handleMoveDown = (index: number) => {
        const categoriesDisabled = [...categoryValue].filter((c) => c.disabled === true);
        const updatedCategories = [...categoryValue].filter((c): c is OrdinalCategoryType => "level" in c).sort(sortCategories);

        updatedCategories[index + categoriesDisabled.length].level++;
        updatedCategories[index + 1 + categoriesDisabled.length].level--;
        setCategoryValue(updatedCategories.sort(sortCategories));
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
                        {ordered && <InputLabel style={{ marginRight: "8px", overflow: "visible" }}>{index + 1}.</InputLabel>}
                        <TextField
                            size="small"
                            fullWidth
                            value={category.name}
                            // error={hasDuplicatedPropertyOption}
                            // helperText={hasDuplicatedPropertyOption ? t("admin.settings.duplicatedOption") : ""}
                            onChange={(e) => handleCategoryChange(index, e.target.value)}
                            placeholder={t("admin.settings.add")}
                            variant="outlined"
                        />
                        {ordered && (
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
            <Stack direction="row" alignItems="center" mt={1}>
                <AddCircleIcon sx={{ cursor: "pointer" }} onClick={handleAddCategory} />
                {t("admin.settings.addOption")}
            </Stack>
        </>
    );
};
