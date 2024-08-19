import { ChangeEvent, useContext, useEffect, useState } from "react";
import { Button, TextField, Grid, Box } from "@mui/material";
import TextInput from "../components/project/inputs/TextInput";
import { Category, getAllCategories, Goal, createGoal, GoalDirection, ConditionFieldType } from "../api/goalsServices";
import { t } from "i18next";
import CategoryInput from "../components/project/inputs/CategoryInput";
import { PropertyRadioGroup } from "../components/goals/PropertyRadioGroup";
import DateInput from "../components/project/inputs/DateInput";
import { Dayjs } from "dayjs";
import AlertContext from "../context/AlertContext";

const emptyGoal = {
    startDate: "",
    endDate: "",
    id: "",
    name: "",
    goalType: "NUMBER",
    goalDirection: "" as GoalDirection,
    goalValue: 0,
    category: {
        id: "",
        name: "",
    },
    conditions: [
        {
            conditionId: "",
            propertyId: "",
            propertyName: "",
            propertyKind: "",
            operator: "",
            value: "",
            conditionFieldType: "" as ConditionFieldType,
            propertyType: "",
            booleanValue: false,
            categoryOptions: [],
            ordinalOptions: { value: 0, min: 0, max: 0 },
            listOptions: [], // Add the missing properties here
            ownershipOptions: [], // Add the missing properties here
        },
    ],
    geography: {
        conditionId: "",
        options: [],
    },
};

const conditionFieldTypeOptions = ["PROPERTY", "GROUND_POSITION", "PROGRAMMING", "HOUSE_TYPE", "OWNERSHIP"];

const goalDirectionOptions = ["MINIMAL", "MAXIMAL", ""];


export function GoalWizard() {
    const [goal, setGoal] = useState<Goal>(emptyGoal);
    const [property, setProperty] = useState<ConditionFieldType>(""); // Align with backend later
    const [categories, setCategories] = useState<Category[]>([]);
    const { setAlert } = useContext(AlertContext);

    useEffect(() => {
        getAllCategories().then((categories) => {
            setCategories(categories);
        });
    }, []);

    const submitForm = async () => {
        try {
            await createGoal(goal);
            setAlert("Goal created", "success");
        } catch (error: unknown) {
            if (error instanceof Error) {
                setAlert(error.message, "error");
            }
        }
    };

    return (
        <Grid container spacing={3}>
            <Grid item xs={12}>
                <Box sx={{ backgroundColor: "grey.300", padding: 2 }}>
                    <TextInput
                        readOnly={false}
                        value={goal.name}
                        setValue={(event: ChangeEvent<HTMLInputElement>) => {
                            const newName = event.target.value.trimStart();
                            setGoal({ ...goal, name: newName });
                        }}
                        mandatory={true}
                        title={t("goals.name")}
                        errorText={t("goals.errors.name")}
                    />
                </Box>
            </Grid>

            <Grid item xs={12}>
                <Box sx={{ height: 16 }} />
            </Grid>

            <Grid item xs={12}>
                <Box sx={{ backgroundColor: "grey.300", padding: 2 }}>
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <CategoryInput
                                readOnly={false}
                                mandatory={false}
                                title={t("goals.property")}
                                options={conditionFieldTypeOptions.map((option) => ({ id: option, name: option }))}
                                values={conditionFieldTypeOptions.find((option) => option === property) || null}
                                setValue={(_, newValue) => {
                                    setProperty(newValue ? newValue.id : "");
                                }}
                                multiple={false}
                                hasTooltipOption={false}
                                error={t("goals.errors.property")}
                                translationPath="goals.properties."
                            />
                        </Grid>

                        <Grid item xs={12}>
                            <PropertyRadioGroup property={property} />
                        </Grid>

                        <Grid container item xs={12} spacing={2}>
                            <Grid item xs={3}>
                                <CategoryInput
                                    readOnly={false}
                                    mandatory={true}
                                    title={t("goals.goalDirection")}
                                    options={goalDirectionOptions.map((option) => ({ id: option, name: option }))}
                                    values={goalDirectionOptions.find((option) => option === goal.goalDirection) || null}
                                    setValue={(_, newValue) => {
                                        setGoal({ ...goal, goalDirection: newValue ? newValue.id : "" });
                                    }}
                                    multiple={false}
                                    hasTooltipOption={false}
                                    error={t("goals.errors.goalDirection")}
                                />
                            </Grid>

                            <Grid item xs={2}>
                                <DateInput
                                    value={goal.startDate || ""}
                                    setValue={(e: Dayjs | null) => {
                                        const newStartDate = e ? e.format("YYYY-MM-DD") : undefined;
                                        setGoal({ ...goal, startDate: newStartDate });
                                    }}
                                    readOnly={false}
                                    mandatory={true}
                                    errorText={t("goals.errors.startDate")}
                                    title={t("goals.startDate")}
                                />
                            </Grid>

                            <Grid item xs={2}>
                                <DateInput
                                    value={goal.endDate || ""}
                                    setValue={(e: Dayjs | null) => {
                                        const newEndDate = e ? e.format("YYYY-MM-DD") : undefined;
                                        setGoal({ ...goal, endDate: newEndDate });
                                    }}
                                    readOnly={false}
                                    mandatory={true}
                                    errorText={t("goals.errors.endDate")}
                                    title={t("goals.endDate")}
                                />
                            </Grid>

                            <Grid item xs={2}>
                                <TextField
                                    fullWidth
                                    label="Geographie"
                                    variant="outlined"
                                    name="goal"
                                    value={goal.geography.conditionId}
                                    onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                                        const newName = event.target.value.trimStart();
                                        setGoal({ ...goal, name: newName });
                                    }}
                                />
                            </Grid>

                            <Grid item xs={3}>
                                <CategoryInput
                                    readOnly={false}
                                    mandatory={false}
                                    title={t("goals.category")}
                                    options={categories}
                                    values={categories.find((option) => option.id === goal.category.id) || null}
                                    setValue={(_, newValue) => {
                                        setGoal({ ...goal, category: { id: newValue ? newValue.id : "", name: newValue ? newValue.name : "" } });
                                    }}
                                    multiple={false}
                                    hasTooltipOption={false}
                                    error={t("goals.errors.category")}
                                />
                            </Grid>
                        </Grid>

                        {/* Buttons */}
                        <Grid item xs={12} style={{ textAlign: "right" }}>
                            <Button variant="outlined" color="secondary" style={{ marginRight: "8px" }}>
                                Annuleren
                            </Button>
                            <Button variant="contained" color="primary" type="submit" onClick={submitForm}>
                                Opslaan
                            </Button>
                        </Grid>
                    </Grid>
                </Box>
            </Grid>
        </Grid>
    );
}
