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
import { conditionFieldTypeOptions, goalDirectionOptions } from "../components/goals/constants";
import { OwnershipRowInputs } from "../components/project-wizard/house-blocks/ownership-information/OwnershipRowInputs";
import { OwnershipSingleValue } from "../types/houseBlockTypes";

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
            listOptions: [],
            ownershipOptions: [
                {
                    type: undefined,
                    //value is not required currently
                    value: {
                        value: 0,
                        min: 0,
                        max: 0,
                    },
                    rangeCategoryOption: {
                        id: "",
                        name: "",
                    },
                },
            ],
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

    const handlePropertyChange = (_: React.ChangeEvent<unknown>, newValue: UpdatedProperty) => {
        if (newValue) {
            const updatedConditions = goal.conditions.map((condition) => {
                return {
                    ...condition,
                    propertyId: newValue.id,
                    propertyName: newValue.name,
                    propertyKind: newValue.propertyKind,
                    propertyType: newValue.propertyType,
                    categoryOptions: [],
                    booleanValue: false,
                };
            });
            setGoal({ ...goal, conditions: updatedConditions });
        }
    };

    const submitForm = async () => {
        const updatedGoal = { ...goal };

        if (!goal.conditions[0] || !goal.conditions[0].conditionFieldType) {
            updatedGoal.conditions = [];
        }
        if (updatedGoal.conditions[0] && updatedGoal.conditions[0].conditionFieldType === "OWNERSHIP") {
            updatedGoal.conditions[0].ownershipOptions = goal.conditions[0].ownershipOptions.map(({ type, valueCategoryId, rentalValueCategoryId}) => {
                return {
                    type,
                    rangeCategoryOption: {
                        id: valueCategoryId || rentalValueCategoryId || "",
                        name: "",
                    },
                };
            });
        } else {
            updatedGoal.conditions[0].ownershipOptions = [];
        }

        try {
            goalId ? await updateGoal(updatedGoal) : await createGoal(updatedGoal);
            setAlert(goalId ? t("goals.notifications.updated") : t("goals.notifications.created"), "success");
            navigate("/goals");
        } catch (error: unknown) {
            if (error instanceof Error) {
                setAlert(error.message, "error");
            }
        }
    };
    //update when conditions are clear
    const disabledButton =
        !goal.name ||
        !goal.startDate ||
        !goal.endDate ||
        !goal.goalDirection ||
        goal.conditions.some((condition) => {
            if (!condition.conditionFieldType) return false;
            if (condition.conditionFieldType === "PROPERTY" && !condition.propertyId) return true;
            if ((condition.conditionFieldType === "GROUND_POSITION" || condition.conditionFieldType === "HOUSE_TYPE") && condition.listOptions.length === 0)
                return true;
            if (condition.propertyType === "CATEGORY" && (!condition.categoryOptions || condition.categoryOptions.length === 0)) return true;
            if (condition.propertyType === "BOOLEAN" && condition.booleanValue === null) return true;
            if (
                condition.conditionFieldType === "OWNERSHIP" &&
                condition.ownershipOptions &&
                condition.ownershipOptions.some((ownershipOption) => ownershipOption.type === undefined)
            )
                return true;
            return false;
        });

    const matchingProperty = goal.conditions[0] ? properties.find((property) => property.id === goal.conditions[0].propertyId) : null;

    const handleInputChange = (index: number, value: OwnershipSingleValue | null) => {
        const updatedValues = [...goal.conditions[0].ownershipOptions];
        updatedValues[index] = value as {
            type: "KOOPWONING" | "HUURWONING_PARTICULIERE_VERHUURDER" | "HUURWONING_WONINGCORPORATIE" | undefined;
            value: { value: number; min: number; max: number };
            rangeCategoryOption: Category | undefined;
        };
        setGoal({
            ...goal,
            conditions: [
                {
                    ...goal.conditions[0],
                    ownershipOptions: updatedValues,
                },
            ],
        });
    };

    //we will need it if more than 1 ownership option is allowed
    // const handleRemoveRow = (index: number) => {
    //     const updatedValues = [...goal.conditions[0].ownershipOptions];
    //     updatedValues.splice(index, 1);
    //     setGoal({
    //         ...goal,
    //         conditions: [
    //             {
    //                 ...goal.conditions[0],
    //                 ownershipOptions: updatedValues,
    //             },
    //         ],
    //     });
    // };

    console.log("goal", goal);

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
                                values={
                                    goal.conditions[0] && goal.conditions[0].conditionFieldType
                                        ? { id: goal.conditions[0].conditionFieldType, name: goal.conditions[0].conditionFieldType }
                                        : null
                                }
                                setValue={(_, newValue) => {
                                    setGoal({
                                        ...goal,
                                        conditions: [
                                            {
                                                ...goal.conditions[0],
                                                conditionFieldType: newValue ? newValue.id : "",
                                                listOptions: [],
                                                categoryOptions: [],
                                                propertyType: undefined,
                                                propertyId: "",
                                                propertyName: "",
                                                propertyKind: undefined,
                                                conditionId: "",
                                                ownershipOptions: [
                                                    { type: undefined, value: { value: 0, min: 0, max: 0 }, rangeCategoryOption: { id: "", name: "" } },
                                                ],
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
                                    options={properties.map((property) => {
                                        return {
                                            id: property.id,
                                            name: property.type === "FIXED" ? t(`admin.settings.fixedPropertyType.${property.name}`) : property.name,
                                            propertyType: property.propertyType,
                                            propertyKind: property.type,
                                        };
                                    })}
                                    values={
                                        goal.conditions[0] && goal.conditions[0].propertyName
                                            ? { id: goal.conditions[0].propertyId, name: goal.conditions[0].propertyName }
                                            : null
                                    }
                                    setValue={handlePropertyChange}
                                    multiple={false}
                                    hasTooltipOption={false}
                                    error={t("goals.errors.selectProperty")}
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

                        {goal.conditions[0] && goal.conditions[0].conditionFieldType === "OWNERSHIP" && (
                            <Grid item xs={12}>
                                {goal.conditions[0]?.ownershipOptions.map((ownershipOption, index) => {
                                    return (
                                        <OwnershipRowInputs
                                            key={index}
                                            isPolicyGoal={true}
                                            index={index}
                                            handleRemoveRow={() => {}}
                                            handleInputChange={handleInputChange}
                                            ownership={ownershipOption}
                                            readOnly={false}
                                            isOwnerShipValueAndMutationConsistent={true}
                                        />
                                    );
                                })}
                            </Grid>
                        )}

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
