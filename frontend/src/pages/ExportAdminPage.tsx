import { useState, ChangeEvent, useEffect } from "react";
import { Grid, Button, Box } from "@mui/material";
import { useTranslation } from "react-i18next";
import TextInput from "../components/project/inputs/TextInput";
import CategoryInput from "../components/project/inputs/CategoryInput";
import { addExportData, ExportData, getExportDataById, updateExportData, Property } from "../api/exportServices";
import useAlert from "../hooks/useAlert";
import { useNavigate, useParams } from "react-router-dom";
import ActionNotAllowed from "./ActionNotAllowed";
import useAllowedActions from "../hooks/useAllowedActions";

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
    const [properties, setProperties] = useState<Property[]>([]);
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

    const handlePropertyChange = (index: number, fieldName: string) => (event: ChangeEvent<HTMLInputElement>) => {
        const updatedProperties = [...properties];
        updatedProperties[index] = {
            ...updatedProperties[index],
            [fieldName]: event.target.value,
        };
        setProperties(updatedProperties);
    };

    const handleAddProperty = () => {
        const newProperty: Property = {
            name: "",
            objectType: "PROJECT",
            mandatory: false,
            options: [],
        };
        setProperties([...properties, newProperty]);
    };

    const handleSubmit = async () => {
        try {
            const exportData: ExportData = {
                id: id || "",
                name: formData.name,
                type: formData.type,
                apiKey: formData.apiKey,
                projectUrl: formData.projectUrl,
                projectdetailUrl: formData.projectdetailUrl,
                properties,
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
        return fields.every((field) => !field.mandatory || formData[field.name].trim() !== "");
    };

    return (
        <Box p={2}>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <CategoryInput
                        values={formData.type}
                        setValue={(event) => setType((event.target as HTMLInputElement).value)}
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

                {properties.map((property, index) => (
                    <Grid item xs={12} key={index}>
                        <TextInput
                            value={property.name}
                            setValue={handlePropertyChange(index, "name")}
                            readOnly={false}
                            mandatory={property.mandatory}
                            title={t("admin.export.property.name")}
                            type="text"
                            errorText={t("admin.export.error.required", { field: t("admin.export.property.name") })}
                        />
                        <TextInput
                            value={property.customPropertyId || ""}
                            setValue={handlePropertyChange(index, "customPropertyId")}
                            readOnly={false}
                            mandatory={false}
                            title={t("admin.export.property.customPropertyId")}
                            type="text"
                        />
                        {property.options?.map((option, optIndex) => (
                            <TextInput
                                key={optIndex}
                                value={option.name}
                                setValue={handlePropertyChange(index, `options[${optIndex}].name`)}
                                readOnly={false}
                                mandatory={false}
                                title={t("admin.export.property.option.name")}
                                type="text"
                            />
                        ))}
                    </Grid>
                ))}

                <Grid item xs={12}>
                    <Button variant="contained" color="primary" onClick={handleSubmit} disabled={!isFormValid()}>
                        {t("generic.save")}
                    </Button>
                </Grid>
            </Grid>
        </Box>
    );
}

export default ExportAdminPage;
