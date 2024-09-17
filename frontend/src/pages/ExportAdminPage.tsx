import { useState, ChangeEvent } from "react";
import { Grid, Button, Box } from "@mui/material";
import { useTranslation } from "react-i18next";
import TextInput from "../components/project/inputs/TextInput";
import CategoryInput from "../components/project/inputs/CategoryInput";

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

function ExportAdminPage() {
    const { t } = useTranslation();

    const typeConfig: TypeConfig = {
        ESRI_ZUID_HOLLAND: {
            fields: [
                { name: "name", label: t("admin.export.name"), type: "text", mandatory: true },
                { name: "apiKey", label: t("API sleutel"), type: "password", mandatory: true },
                { name: "projectUrl", label: t("Create layer URL"), type: "text", mandatory: false },
                { name: "projectdetailUrl", label: t("Project details URL"), type: "text", mandatory: false },
            ],
        },
        // TEST_CONFIG: {
        //     fields: [
        //         { name: "testName", label: "Test Naam", type: "text", mandatory: true },
        //         { name: "testApiKey", label: "Test API sleutel", type: "password", mandatory: true },
        //         { name: "testUrl", label: "Test URL", type: "text", mandatory: false },
        //         { name: "testDetailUrl", label: "Test details URL", type: "text", mandatory: false },
        //         { name: "blabla", label: "blabla", type: "text", mandatory: false },
        //     ],
        // },
        // Other types can be added here in the future
    };

    interface FormData {
        [key: string]: string;
    }

    const generateInitialState = (type: string): FormData => {
        const fields = typeConfig[type]?.fields || [];
        const initialState: FormData = { type };
        fields.forEach((field) => {
            initialState[field.name] = "";
        });
        return initialState;
    };

    const [formData, setFormData] = useState<FormData>(generateInitialState("ESRI_ZUID_HOLLAND"));
    const handleChange = (fieldName: string) => (event: ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [fieldName]: event.target.value,
        });
    };
    const handleSubmit = () => {
        console.log(formData);
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
