import { ChangeEvent, useContext, useEffect, useState } from "react";
import { Button, Grid, Box, ToggleButtonGroup, ToggleButton } from "@mui/material";
import TextInput from "../components/project/inputs/TextInput";
import { styled } from "@mui/material/styles";
import { Goal, createGoal, GoalDirection, ConditionFieldType, PropertyKind, getGoal, updateGoal, GoalType, Category } from "../api/goalsServices";
import { t } from "i18next";
import CategoryInput from "../components/project/inputs/CategoryInput";
import DateInput from "../components/project/inputs/DateInput";
import { Dayjs } from "dayjs";
import AlertContext from "../context/AlertContext";
import { PropertyType } from "../types/enums";
import { useNavigate, useParams } from "react-router-dom";
import { getCustomProperties, Property } from "../api/adminSettingServices";
import { CustomPropertyWidget } from "../components/CustomPropertyWidget";
import CategoryAutocomplete from "../components/goals/CategoryAutocomplete";
import { SingleNumberInput } from "../components/project/inputs/SingleNumberInput";
import { PropertyRadioGroup } from "../components/goals/PropertyRadioGroup";

const emptyGoal = {
    startDate: "",
    endDate: "",
    id: "",
    name: "",
    goalType: "NUMBER" as GoalType,
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
            propertyKind: "FIXED" as PropertyKind,
            conditionFieldType: "" as ConditionFieldType,
            propertyType: "CATEGORY" as PropertyType,
            booleanValue: false,
            categoryOptions: [],
            ordinalOptions: {
                value: { id: "", name: "" },
                min: { id: "", name: "" },
                max: { id: "", name: "" },
            },
            listOptions: [], // Add the missing properties here
            ownershipOptions: [], // Add the missing properties here
        },
    ],
    geography: {
        conditionId: "",
        options: [],
    },
};

type UpdatedProperty = {
    id: string;
    name: string;
    propertyKind: PropertyKind;
    propertyType: PropertyType;
};

const conditionFieldTypeOptions = ["PROPERTY", "GROUND_POSITION", "PROGRAMMING", "HOUSE_TYPE", "OWNERSHIP"];

const goalDirectionOptions = ["MINIMAL", "MAXIMAL", ""];

const SmallToggleButton = styled(ToggleButton)(({ theme }) => ({
    padding: theme.spacing(0.5),
    fontSize: "0.75rem",
    minWidth: "auto",
    maxHeight: "20px",
    backgroundColor: theme.palette.common.white,
    color: theme.palette.text.primary,
    border: `1px solid black`,
    textTransform: "none",
    "&.Mui-selected": {
        backgroundColor: theme.palette.primary.main,
        color: theme.palette.common.white,
        "&:hover": {
            backgroundColor: theme.palette.primary.dark,
        },
    },
    "&:hover": {
        backgroundColor: theme.palette.action.hover,
    },
}));

export function GoalWizard() {
    const { goalId } = useParams<{ goalId: string }>();
    const [goal, setGoal] = useState<Goal>(emptyGoal);
    const [properties, setProperties] = useState<Property[]>([]);
    const { setAlert } = useContext(AlertContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (goalId) {
            getGoal(goalId).then((goal) => {
                setGoal(goal);
            });
        }
    }, [goalId]);

    const isNumberGoal = goal.goalType === "NUMBER";

    useEffect(() => {
        const updatedGoal = { ...goal };

        if (!goal.conditions[0] || !goal.conditions[0].conditionFieldType) {
            updatedGoal.conditions = [];
        }

        if (isNumberGoal) {
            updatedGoal.goalDirection = "MAXIMAL";
        }

        setGoal(updatedGoal);
    }, [goal, isNumberGoal]);

    useEffect(() => {
        getCustomProperties().then((properties) => {
            const filteredProperties = properties.filter(
                //Supported property types are boolean and categorical at the moment.
                (property) => !property.disabled && (property.propertyType === "CATEGORY" || property.propertyType === "BOOLEAN"),
            );
            setProperties(filteredProperties);
        });
    }, []);

    const handleGoalTypeChange = (event: React.MouseEvent<HTMLElement>, newGoalType: GoalType) => {
        if (newGoalType !== null && event) {
            setGoal({ ...goal, goalType: newGoalType });
        }
    };

    const handlePropertyChange = (_: React.ChangeEvent<{}>, newValue: UpdatedProperty) => {
        if (newValue) {
            const updatedConditions = goal.conditions.map((condition, index) => {
                return {
                    ...condition,
                    propertyId: newValue.id,
                    propertyName: newValue.name,
                    propertyKind: newValue.propertyKind,
                    propertyType: newValue.propertyType,
                };
            });
            setGoal({ ...goal, conditions: updatedConditions });
        }
    };

    const submitForm = async () => {
        try {
            goalId ? await updateGoal(goal) : await createGoal(goal);
            setAlert(goalId ? t("goals.notifications.updated") : t("goals.notifications.created"), "success");
            navigate("/goals");
        } catch (error: unknown) {
            if (error instanceof Error) {
                setAlert(error.message, "error");
            }
        }
    };
    //update when conditions are clear
    const disabledButton = !goal.name || !goal.startDate || !goal.endDate || !goal.goalDirection;

    const matchingProperty = goal.conditions[0] ? properties.find((property) => property.id === goal.conditions[0].propertyId) : null;

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
                                values={goal.conditions[0] && goal.conditions[0].conditionFieldType ? goal.conditions[0].conditionFieldType : ""}
                                setValue={(_, newValue) => {
                                    setGoal({
                                        ...goal,
                                        conditions: [
                                            {
                                                ...goal.conditions[0],
                                                conditionFieldType: newValue ? newValue.id : "",
                                            },
                                        ],
                                    });
                                }}
                                multiple={false}
                                hasTooltipOption={false}
                                error={t("goals.errors.property")}
                                translationPath="goals.properties."
                            />
                        </Grid>

                        {goal.conditions[0] && goal.conditions[0].conditionFieldType === "PROPERTY" && (
                            <Grid item xs={12}>
                                <CategoryInput
                                    readOnly={false}
                                    mandatory={true}
                                    title={t("goals.selectProperty")}
                                    options={properties.map((property) => ({
                                        id: property.id,
                                        name: property.name,
                                        propertyType: property.propertyType,
                                        propertyKind: property.type,
                                    }))}
                                    values={goal.conditions[0] && goal.conditions[0].propertyName ? goal.conditions[0].propertyName : ""}
                                    setValue={handlePropertyChange}
                                    multiple={false}
                                    hasTooltipOption={false}
                                    error={t("goals.errors.selectProperty")}
                                    translationPath="admin.settings.fixedPropertyType."
                                />
                            </Grid>
                        )}
                        {goal.conditions[0] && goal.conditions[0].propertyId && goal.conditions[0].conditionFieldType === "PROPERTY" && (
                            <Grid item xs={12}>
                                <CustomPropertyWidget
                                    readOnly={false}
                                    customValue={{
                                        ...goal.conditions[0],
                                        categories: goal.conditions[0].categoryOptions?.map((option) => option.id),
                                    }}
                                    setCustomValue={(newValue) => {
                                        if (goal.conditions[0].propertyType === "BOOLEAN") {
                                            setGoal({
                                                ...goal,
                                                conditions: [
                                                    {
                                                        ...goal.conditions[0],
                                                        booleanValue: newValue.booleanValue || false,
                                                    },
                                                ],
                                            });
                                        } else if (goal.conditions[0].propertyType === "CATEGORY") {
                                            const matchedProperty = properties.find((property) => property.id === goal.conditions[0].propertyId);
                                            if (matchedProperty && matchedProperty.categories) {
                                                const updatedCategories = (newValue.categories ?? [])
                                                    .map((id) => {
                                                        const category = matchedProperty.categories?.find((cat) => cat.id === id);
                                                        if (category) {
                                                            const { id, name } = category;
                                                            return { id, name };
                                                        }
                                                        return undefined;
                                                    })
                                                    .filter((category) => category !== undefined) as Category[];
                                                setGoal({
                                                    ...goal,
                                                    conditions: [
                                                        {
                                                            ...goal.conditions[0],
                                                            categoryOptions: updatedCategories,
                                                        },
                                                    ],
                                                });
                                            }
                                        }
                                    }}
                                    customDefinition={
                                        matchingProperty || {
                                            id: undefined,
                                            name: "",
                                            type: "FIXED",
                                            objectType: "PROJECT",
                                            propertyType: "CATEGORY",
                                            disabled: false,
                                        }
                                    }
                                />
                            </Grid>
                        )}
                        <Grid item xs={12}>
                            <PropertyRadioGroup property={goal.conditions[0] ? goal.conditions[0].conditionFieldType : ""} setGoal={setGoal} goal={goal} />
                        </Grid>

                        <Grid container item xs={12} spacing={2}>
                            <Grid item xs={2}>
                                <Box sx={{ position: "relative" }}>
                                    <SingleNumberInput
                                        acceptsDecimal={goal.goalType === "PERCENTAGE"}
                                        isInputLabel={true}
                                        value={goal.goalValue}
                                        onChange={(e) => {
                                            if (e) {
                                                setGoal({
                                                    ...goal,
                                                    goalValue: e,
                                                });
                                            }
                                        }}
                                        readOnly={false}
                                        mandatory={false}
                                        name={t("goals.goalValue")}
                                    />
                                    <ToggleButtonGroup
                                        value={goal.goalType}
                                        exclusive
                                        onChange={handleGoalTypeChange}
                                        aria-label="goal type"
                                        sx={{ position: "absolute", right: 0, top: 5 }}
                                    >
                                        <SmallToggleButton value="NUMBER" aria-label="number">
                                            {t("goals.goalType.number")}
                                        </SmallToggleButton>
                                        <SmallToggleButton value="PERCENTAGE" aria-label="percentage">
                                            {t("goals.goalType.percentage")}
                                        </SmallToggleButton>
                                    </ToggleButtonGroup>
                                </Box>
                            </Grid>
                            <Grid item xs={2}>
                                <CategoryInput
                                    readOnly={isNumberGoal ? true : false}
                                    mandatory={true}
                                    title={t("goals.goalDirection")}
                                    options={goalDirectionOptions.map((option) => ({ id: option, name: option }))}
                                    values={
                                        isNumberGoal
                                            ? { id: "MAXIMAL", name: "MAXIMAL" }
                                            : goalDirectionOptions.find((option) => option === goal.goalDirection) || null
                                    }
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

                            {/*GEOGRAPHY*/}
                            {/* <Grid item xs={2}>
                                <TextField
                                    fullWidth
                                    label="Geographie"
                                    variant="outlined"
                                    name="goal"
                                    value={goal.geography ? goal.geography.conditionId : ""}
                                    onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                                        const newName = event.target.value.trimStart();
                                        setGoal({ ...goal, name: newName });
                                    }}
                                />
                            </Grid> */}

                            <Grid item xs={3}>
                                <CategoryAutocomplete goal={goal} setGoal={setGoal} />
                            </Grid>
                        </Grid>
                        <Grid item xs={12} style={{ textAlign: "right" }}>
                            <Button variant="outlined" color="primary" style={{ marginRight: "8px" }} onClick={() => navigate("/goals")}>
                                {t("generic.cancel")}
                            </Button>
                            <Button variant="contained" color="primary" type="submit" onClick={submitForm} disabled={disabledButton}>
                                {t("generic.save")}
                            </Button>
                        </Grid>
                    </Grid>
                </Box>
            </Grid>
        </Grid>
    );
}
