import { useState, ChangeEvent, useEffect, useContext } from "react";
import { Grid, Button, Box } from "@mui/material";
import { useTranslation } from "react-i18next";
import TextInput from "../components/project/inputs/TextInput";
import CategoryInput from "../components/project/inputs/CategoryInput";
import { addExportData, ExportData, getExportDataById, updateExportData, ExportProperty, ValidationError } from "../api/exportServices";
import useAlert from "../hooks/useAlert";
import { useNavigate, useParams } from "react-router-dom";
import ActionNotAllowed from "./ActionNotAllowed";
import { getCustomProperties, Property } from "../api/adminSettingServices";
import { CustomPropertyWidget } from "../components/CustomPropertyWidget";
import { LabelComponent } from "../components/project/LabelComponent";
import { exportSettings, updateExportSettings } from "../Paths";
import UserContext from "../context/UserContext";
import { doesPropertyMatchExportProperty } from "../utils/exportUtils";

type SelectedOption = {
    id: string;
    name: string;
    propertyType: string;
    type: string;
};

type FieldConfig = {
    name: string;
    label: string;
    type: string;
    mandatory: boolean;
};

type TypeConfig = {
    [key: string]: {
        fields: FieldConfig[];
    };
};

type FormData = {
    [key: string]: string;
};

function ExportAdminPage() {
    const { t } = useTranslation();
    const { id } = useParams<string>();
    const { allowedActions } = useContext(UserContext);
    const navigate = useNavigate();
    const typeConfig: TypeConfig = {
        ESRI_ZUID_HOLLAND: {
            fields: [{ name: "name", label: t("admin.export.name"), type: "text", mandatory: true }],
        },
        // Other types can be added here in the future
    };
    const [type, setType] = useState<string>("ESRI_ZUID_HOLLAND");
    const [formData, setFormData] = useState<FormData>(generateInitialState(type));
    const [properties, setProperties] = useState<ExportProperty[]>([]);
    const [customProperties, setCustomProperties] = useState<Property[]>([]);
    const { setAlert } = useAlert();
    const [validationErrors, setValidationErrors] = useState<ValidationError[]>([]);

    useEffect(() => {
        if (id) {
            const fetchData = async () => {
                const data = await getExportDataById(id);
                const { properties, valid, validationErrors, ...formData } = data;
                setFormData(formData);
                setProperties(properties || []);
            };
            fetchData();
        }
    }, [id]);

    useEffect(() => {
        getCustomProperties().then((properties) => {
            const filteredProperties = properties.filter((property) => !property.disabled);
            setCustomProperties(filteredProperties);
        });
    }, []);

    if (!allowedActions.includes("EDIT_DATA_EXCHANGES")) {
        return <ActionNotAllowed errorMessage={t("admin.export.actionNotAllowed")} />;
    }

    function generateInitialState(type: string): FormData {
        const fields = typeConfig[type]?.fields || [];
        const initialState: FormData = { type };
        fields.forEach((field) => {
            initialState[field.name] = "";
        });
        return initialState;
    }

    const handleChange = (fieldName: string) => (event: ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [fieldName]: event.target.value,
        });
    };

    const handleSubmit = async () => {
        try {
            const exportData: ExportData = {
                id: id || "",
                name: formData.name,
                type: formData.type,
                ...(formData.apiKey && { apiKey: formData.apiKey }),
                projectUrl: formData.projectUrl,
                ...(id && { properties }),
            };
            const data = id ? await updateExportData(id, exportData) : await addExportData(exportData);
            setValidationErrors(data.validationErrors || []);
            setAlert(id ? t("admin.export.notification.updated") : t("admin.export.notification.created"), "success");
            if (!id) {
                navigate(updateExportSettings.toPath({ id: data.id }));
            }
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "error");
        }
    };

    const fields = typeConfig[formData.type]?.fields || [];

    const isFormValid = () => {
        return fields.every((field) => !field.mandatory || formData[field.name]?.trim() !== "");
    };

    const handlePropertyChange = (index: number, value: SelectedOption | null) => {
        const updatedProperties = [...properties];

        if (value) {
            updatedProperties[index].customPropertyId = value.id;
        } else {
            updatedProperties[index].customPropertyId = null;
            updatedProperties[index].options?.forEach((option) => {
                option.propertyCategoryValueIds = [];
                option.propertyOrdinalValueIds = [];
            });
        }

        setProperties(updatedProperties);
    };

    const mapPropertyToCustomDefinition = (property: ExportProperty, selectedProperty: Property): Property => {
        const matchingCustomProperty = customProperties.find((customProperty) => customProperty.id === selectedProperty.id);

        const categoriesOrOrdinals = matchingCustomProperty?.categories?.length
            ? matchingCustomProperty.categories
            : matchingCustomProperty?.ordinals?.length
              ? matchingCustomProperty.ordinals
              : [];
        return {
            id: property.id,
            name: property.name,
            type: "CUSTOM",
            objectType: property.objectType,
            propertyType: property.propertyTypes[0] as "BOOLEAN" | "CATEGORY" | "ORDINAL" | "NUMERIC" | "TEXT" | "RANGE_CATEGORY",
            disabled: false,
            categories: categoriesOrOrdinals.filter((item) => !item.disabled),
            singleSelect: property.singleSelect,
            mandatory: property.mandatory,
        };
    };

    return (
        <Box p={2} sx={{ backgroundColor: "#F0F0F0" }}>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <CategoryInput
                        values={formData.type}
                        setValue={(event, value) => setType(value)}
                        readOnly={false}
                        mandatory={true}
                        title="Type"
                        options={[{ name: "ESRI_ZUID_HOLLAND", id: "ESRI_ZUID_HOLLAND" }]}
                        multiple={false}
                    />
                </Grid>

                {fields.map((field) => (
                    <Grid item xs={12} key={field.name}>
                        <TextInput
                            value={formData[field.name]}
                            setValue={handleChange(field.name)}
                            readOnly={false}
                            mandatory={field.mandatory}
                            title={field.label}
                            type={field.type}
                            errorText={t("admin.export.error.required", { field: field.label })}
                        />
                    </Grid>
                ))}

                {properties.map((property, index) => {
                    const selectedOption = customProperties.find((customProperty) => customProperty.id === property.customPropertyId);
                    const error = validationErrors.find((error) => error.dxProperty === property.name);
                    return (
                        <Grid item xs={12} key={index}>
                            <LabelComponent
                                text={
                                    t(`exchangeData.labels.${type}.${property.name}`) +
                                    ` (${property.name}, type: ${property.propertyTypes.map((type) => t(`admin.settings.propertyType.${type}`)).join(", ")})`
                                }
                                required={false}
                                disabled={false}
                            />
                            <CategoryInput
                                readOnly={false}
                                mandatory={false}
                                options={customProperties
                                    .filter((customProperty) => doesPropertyMatchExportProperty(property, customProperty))
                                    .map((property) => {
                                        return {
                                            id: property.id,
                                            name: property.type === "FIXED" ? t(`admin.settings.fixedPropertyType.${property.name}`) : property.name,
                                            propertyType: property.propertyType,
                                            type: property.type,
                                        };
                                    })}
                                values={
                                    selectedOption
                                        ? {
                                              id: selectedOption.id,
                                              name:
                                                  selectedOption.type === "FIXED"
                                                      ? t(`admin.settings.fixedPropertyType.${selectedOption.name}`)
                                                      : selectedOption.name,
                                              propertyType: selectedOption.propertyType,
                                              type: selectedOption.type,
                                          }
                                        : null
                                }
                                setValue={(event, value) => handlePropertyChange(index, value)}
                                multiple={false}
                                hasTooltipOption={false}
                                displayError={error ? true : false}
                                error={error ? t(`exchangeData.validationErrors.${error.errorCode}`) : ""}
                            />
                            {selectedOption &&
                                (selectedOption.propertyType === "CATEGORY" || selectedOption.propertyType === "ORDINAL") &&
                                property?.options?.map((option, optionIndex) => (
                                    <Box key={optionIndex} marginLeft={10}>
                                        <LabelComponent text={option.name} required={false} disabled={false} />
                                        <CustomPropertyWidget
                                            readOnly={false}
                                            customValue={
                                                property.options &&
                                                property.options[optionIndex] && { categories: property.options[optionIndex].propertyCategoryValueIds }
                                            }
                                            customDefinition={mapPropertyToCustomDefinition(property, selectedOption)}
                                            setCustomValue={(newValue) => {
                                                const updatedProperties = [...properties];
                                                if (updatedProperties[index].options) {
                                                    if (newValue.categories) {
                                                        updatedProperties[index].options[optionIndex].propertyCategoryValueIds = newValue.categories;
                                                    }
                                                }
                                                setProperties(updatedProperties);
                                            }}
                                            isExportPage={true}
                                        />
                                    </Box>
                                ))}
                        </Grid>
                    );
                })}

                <Grid item xs={12} sx={{ mb: 10, display: "flex", flexDirection: "row", gap: 2, justifyContent: "flex-end" }}>
                    <Button variant="outlined" color="primary" onClick={() => navigate(exportSettings.toPath())}>
                        {t("generic.cancel")}
                    </Button>

                    <Button variant="contained" color="primary" onClick={handleSubmit} disabled={!isFormValid()}>
                        {t("generic.save")}
                    </Button>
                </Grid>
            </Grid>
        </Box>
    );
}

export default ExportAdminPage;
