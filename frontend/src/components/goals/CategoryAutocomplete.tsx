import { useEffect, useState } from "react";
import { Autocomplete, TextField, IconButton, InputAdornment } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import { createCategory, CustomCategory, deleteCategory, getAllCategories, Goal } from "../../api/goalsServices";
import InputLabelStack from "../project/inputs/InputLabelStack";
import { t } from "i18next";

type Props = {
    goal: Goal;
    setGoal: (goal: Goal) => void;
};

const CategoryAutocomplete = ({ goal, setGoal }: Props) => {
    const [inputValue, setInputValue] = useState("");
    const [categories, setCategories] = useState<CustomCategory[]>([]);

    useEffect(() => {
        getAllCategories().then((categories) => {
            setCategories(categories);
        });
    }, []);

    const categoryExists = categories.some((category) => category.name.toLowerCase() === inputValue.toLowerCase());

    const handleCategoryChange = (_: React.ChangeEvent<unknown>, newValue: string | CustomCategory | null) => {
        if (typeof newValue === "string") {
            setGoal({ ...goal, category: null });
        } else {
            setGoal({ ...goal, category: newValue });
        }
    };

    const handleAddCategory = () => {
        if (categoryExists || inputValue.trim() === "") return;
        createCategory({ name: inputValue }).then((newCategory) => {
            setCategories([...categories, newCategory]);
            setGoal({ ...goal, category: newCategory });
            setInputValue("");
        });
    };

    const handleDeleteCategory = (categoryId: string) => {
        deleteCategory(categoryId).then(() => {
            setCategories(categories.filter((category) => category.id !== categoryId));
            setGoal({ ...goal, category: null });
            setInputValue("");
        });
    };

    return (
        <InputLabelStack mandatory={false} title={t("goals.category")}>
            <Autocomplete
                freeSolo
                value={goal.category || null}
                onChange={handleCategoryChange}
                inputValue={inputValue}
                onInputChange={(_, newInputValue) => setInputValue(newInputValue)}
                options={categories}
                getOptionLabel={(option) => (typeof option === "string" ? option : option.name)}
                renderOption={(props, option) => (
                    <li {...props} key={option.id} style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        {option.name}
                        <IconButton onClick={() => option.id && handleDeleteCategory(option.id)} size="small">
                            <DeleteIcon fontSize="small" />
                        </IconButton>
                    </li>
                )}
                isOptionEqualToValue={(option, value) => option.id === value.id}
                renderInput={(params) => (
                    <TextField
                        {...params}
                        size="small"
                        InputProps={{
                            ...params.InputProps,
                            endAdornment: !categoryExists && inputValue && (
                                <InputAdornment position="end">
                                    <IconButton onClick={handleAddCategory} color="primary">
                                        <AddIcon />
                                    </IconButton>
                                </InputAdornment>
                            ),
                        }}
                    />
                )}
            />
        </InputLabelStack>
    );
};

export default CategoryAutocomplete;
