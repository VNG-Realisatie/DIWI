import { useState, ChangeEvent, useEffect, useContext, useCallback, useMemo } from "react";
import { Grid, Button, Box, Alert } from "@mui/material";
import { useTranslation } from "react-i18next";
import TextInput from "../components/project/inputs/TextInput";
import CategoryInput from "../components/project/inputs/CategoryInput";
import {
    addExportData,
    ExportData,
    getExportDataById,
    updateExportData,
    ExportProperty,
    ValidationError,
    getExportTypes,
    ExportType,
} from "../api/exportServices";
import useAlert from "../hooks/useAlert";
import { useNavigate, useParams } from "react-router-dom";
import ActionNotAllowed from "./ActionNotAllowed";
import { Property } from "../api/adminSettingServices";
import { CustomPropertyWidget } from "../components/CustomPropertyWidget";
import { LabelComponent } from "../components/project/LabelComponent";
import { exportSettings, updateExportSettings } from "../Paths";
import UserContext from "../context/UserContext";
import { doesPropertyMatchExportProperty } from "../utils/exportUtils";
import { useCustomPropertyStore } from "../hooks/useCustomPropertyStore";

type ExportOption = {
    id: ExportType;
    name: string;
};

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

const initialType = "ESRI_ZUID_HOLLAND";

function ExportAdminPage() {
    const { t } = useTranslation();
    const { id } = useParams<string>();
    const { allowedActions } = useContext(UserContext);
    const navigate = useNavigate();
    const [exportTypes, setExportTypes] = useState<ExportType[]>([]);

    //can be mapped using exportTypes, but fields need to be clarified, so hardcoded for now.
    const typeConfig: TypeConfig = useMemo(
        () => ({
            ESRI_ZUID_HOLLAND: {
                fields: [{ name: "name", label: t("admin.export.name"), type: "text", mandatory: true }],
            },
            GEO_JSON: {
                fields: [{ name: "name", label: t("admin.export.name"), type: "text", mandatory: true }],
            },
            EXCEL: {
                fields: [{ name: "name", label: t("admin.export.name"), type: "text", mandatory: true }],
            },
        }),
        [t],
    );

    const [type, setType] = useState<ExportOption>({
        id: initialType,
        name: t(`admin.export.${initialType}`),
    });

    const generateInitialState = useCallback(
        (type: ExportOption): FormData => {
            const fields = typeConfig[type.id]?.fields || [];
            const initialState: FormData = { type: type.id };
            fields.forEach((field) => {
                initialState[field.name] = "";
            });
            return initialState;
        },
        [typeConfig],
    );

    const [formData, setFormData] = useState<FormData>();
    const [properties, setProperties] = useState<ExportProperty[]>([]);
    const { setAlert } = useAlert();
    const [validationErrors, setValidationErrors] = useState<ValidationError[]>([]);
    const { customProperties: unfilteredCustomProperties, fetchCustomProperties } = useCustomPropertyStore();

    useEffect(() => {
        if (id) return;
        setFormData(generateInitialState(type));
    }, [type, id, generateInitialState]);

    useEffect(() => {
        getExportTypes().then((data) => {
            setExportTypes(data);
        });
    }, []);

    useEffect(() => {
        fetchCustomProperties();
    }, [fetchCustomProperties]);

    useEffect(() => {
        if (id) {
            const fetchData = async () => {
                const data = await getExportDataById(id);
                const { properties, valid, validationErrors, ...formData } = data;
                setFormData(formData);
                setProperties(properties || []);
                setType({ id: data.type as ExportType, name: t(`admin.export.${data.type}`) });
            };
            fetchData();
        }
    }, [id, t]);

    const customProperties = unfilteredCustomProperties.filter((property) => !property.disabled);

    if (!allowedActions.includes("EDIT_DATA_EXCHANGES")) {
        return <ActionNotAllowed errorMessage={t("admin.export.actionNotAllowed")} />;
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
                name: formData?.name ?? "",
                type: type.id,
                ...(formData?.apiKey && { apiKey: formData.apiKey }),
                projectUrl: formData?.projectUrl ?? "",
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

    const fields = typeConfig[formData?.type || ""]?.fields || [];

    const isFormValid = () => {
        return fields.every((field) => !field.mandatory || (typeof formData?.[field.name] === "string" && formData?.[field.name]?.trim() !== ""));
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
                        values={type}
                        setValue={(event, value) => setType({ id: value.id, name: value.name })}
                        readOnly={formData?.id ? true : false}
                        mandatory={true}
                        title="Type"
                        options={exportTypes.map((type) => {
                            return {
                                id: type,
                                name: t(`admin.export.${type}`),
                            };
                        })}
                        multiple={false}
                    />
                </Grid>

                {fields.map((field) => (
                    <Grid item xs={12} key={field.name}>
                        <TextInput
                            value={formData?.[field.name] ?? ""}
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
                                    t(`exchangeData.labels.${type.id}.${property.name}`) +
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
                    {validationErrors.length > 0 && <Alert severity="warning">{t("exchangeData.validationErrors.genericError")}</Alert>}
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
