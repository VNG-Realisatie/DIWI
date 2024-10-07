import { useState, ChangeEvent, useEffect } from "react";
import { Grid, Button, Box } from "@mui/material";
import { useTranslation } from "react-i18next";
import TextInput from "../components/project/inputs/TextInput";
import CategoryInput from "../components/project/inputs/CategoryInput";
import { addExportData, ExportData, getExportDataById, updateExportData, ExportProperty } from "../api/exportServices";
import useAlert from "../hooks/useAlert";
import { useNavigate, useParams } from "react-router-dom";
import ActionNotAllowed from "./ActionNotAllowed";
import useAllowedActions from "../hooks/useAllowedActions";
import { getCustomProperties, Property } from "../api/adminSettingServices";
import { CustomPropertyWidget } from "../components/CustomPropertyWidget";

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
    const { allowedActions } = useAllowedActions();
    const navigate = useNavigate();
    const typeConfig: TypeConfig = {
        ESRI_ZUID_HOLLAND: {
            fields: [
                { name: "name", label: t("admin.export.name"), type: "text", mandatory: true },
                { name: "apiKey", label: t("admin.export.apiKey"), type: "password", mandatory: true },
                { name: "projectUrl", label: t("admin.export.projectUrl"), type: "text", mandatory: false },
                { name: "projectDetailUrl", label: t("admin.export.projectdetailUrl"), type: "text", mandatory: false },
            ],
        },
        // Other types can be added here in the future
    };
    const [type, setType] = useState<string>("ESRI_ZUID_HOLLAND");
    const [formData, setFormData] = useState<FormData>(generateInitialState(type));
    const [properties, setProperties] = useState<ExportProperty[]>([]);
    const [customProperties, setCustomProperties] = useState<Property[]>([]);
    const { setAlert } = useAlert();

    useEffect(() => {
        if (id) {
            const fetchData = async () => {
                const data = await getExportDataById(id);
                const { properties, ...formData } = data;
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
                projectdetailUrl: formData.projectdetailUrl,
                ...(id && { properties }),
            };
            id ? await updateExportData(id, exportData) : await addExportData(exportData);
            setAlert(id ? t("admin.export.notification.updated") : t("admin.export.notification.created"), "success");
            navigate("/exchangedata/export");
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "error");
        }
    };

    const fields = typeConfig[formData.type]?.fields || [];

    const isFormValid = () => {
        return fields.every((field) => !field.mandatory || formData[field.name]?.trim() !== "");
    };

    const handlePropertyChange = (index: number, value: any) => {

        const updatedProperties = [...properties];

        updatedProperties[index].options?.map((option) => {
            option.propertyCategoryValueIds = [];
        });

        updatedProperties[index].customPropertyId = value.id;
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
            categories: categoriesOrOrdinals.map((item) => ({ ...item, disabled: false })),
            singleSelect: property.singleSelect,
        };
    };

    return (
        <Box p={2}>
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
                    return (
                        <Grid item xs={12} key={index}>
                            <TextInput
                                value={property.name}
                                setValue={() => {}}
                                readOnly={true}
                                mandatory={property.mandatory}
                                type="text"
                                errorText={t("admin.export.error.required", { field: t("admin.export.property.name") })}
                            />
                            <CategoryInput
                                readOnly={false}
                                mandatory={property.mandatory}
                                options={customProperties
                                    .filter(
                                        (customProperty) =>
                                            property.propertyTypes.some((type) => type === customProperty.propertyType) &&
                                            property.objectType === customProperty.objectType,
                                    )
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
                                error={t("goals.errors.selectProperty")}
                            />
                            {selectedOption &&
                                (selectedOption.propertyType === "CATEGORY" || selectedOption.propertyType === "ORDINAL") &&
                                property?.options?.map((option, optionIndex) => (
                                    <Box key={optionIndex} marginLeft={10}>
                                        <TextInput value={option.name} setValue={() => {}} readOnly={true} mandatory={false} type="text" />
                                        <CustomPropertyWidget
                                            readOnly={false}
                                            customValue={
                                                property.options &&
                                                property.options[optionIndex] && { categories: property.options[optionIndex].propertyCategoryValueIds }
                                            }
                                            customDefinition={mapPropertyToCustomDefinition(property, selectedOption)}
                                            setCustomValue={(newValue) => {
                                                console.log(newValue);
                                                const updatedProperties = [...properties];
                                                if (updatedProperties[index].options) {
                                                    if (newValue.categories) {
                                                        updatedProperties[index].options[optionIndex].propertyCategoryValueIds = newValue.categories;
                                                    }
                                                }
                                                setProperties(updatedProperties);
                                            }}
                                        />
                                    </Box>
                                ))}
                        </Grid>
                    );
                })}

                <Grid item xs={12} sx={{ mb: 10 }}>
                    <Button variant="contained" color="primary" onClick={handleSubmit} disabled={!isFormValid()}>
                        {t("generic.save")}
                    </Button>
                </Grid>
            </Grid>
        </Box>
    );
}

export default ExportAdminPage;
