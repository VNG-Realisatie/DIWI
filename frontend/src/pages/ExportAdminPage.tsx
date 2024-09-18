import { useState, ChangeEvent, useEffect } from "react";
import { Grid, Button, Box } from "@mui/material";
import { useTranslation } from "react-i18next";
import TextInput from "../components/project/inputs/TextInput";
import CategoryInput from "../components/project/inputs/CategoryInput";
import { addExportData, ExportData, getExportDataById, updateExportData } from "../api/exportServices";
import useAlert from "../hooks/useAlert";
import { useParams } from "react-router-dom";
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

interface FormData {
    [key: string]: string;
}

function ExportAdminPage() {
    const { t } = useTranslation();
    const { id } = useParams<string>();
    const { allowedActions } = useAllowedActions();
    const typeConfig: TypeConfig = {
        ESRI_ZUID_HOLLAND: {
            fields: [
                { name: "name", label: t("admin.export.name"), type: "text", mandatory: true },
                { name: "apiKey", label: t("admin.export.apiKey"), type: "password", mandatory: true },
                { name: "projectUrl", label: t("admin.export.projectUrl"), type: "text", mandatory: false },
                { name: "projectdetailUrl", label: t("admin.export.projectdetailUrl"), type: "text", mandatory: false },
            ],
        },
        // Other types can be added here in the future
    };
    const [formData, setFormData] = useState<FormData>(generateInitialState("ESRI_ZUID_HOLLAND"));
    const { setAlert } = useAlert();

    useEffect(() => {
        if (id) {
            const fetchData = async () => {
                const data = await getExportDataById(id);
                setFormData(data);
            };
            fetchData();
        }
    }, [id]);

    if (!allowedActions.includes("EXPORT_PROJECTS")) {
        return <ActionNotAllowed errorMessage={t("admin.export.actionNotAllowed")}/>
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
                ...formData,
            };
            id ? await updateExportData(id, exportData) : await addExportData(exportData);
            setAlert(id ? t("admin.export.notification.updated") : t("admin.export.notification.created"), "success");
            setFormData(generateInitialState("ESRI_ZUID_HOLLAND"));
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
                        setValue={() => {}}
                        readOnly={true}
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
