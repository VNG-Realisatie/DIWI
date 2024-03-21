import { Grid, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { CustomPropertyType, getCustomPropertiesWithQuery } from "../api/adminSettingServices";
import { columnTitleStyle } from "./project/project-with-house-block/ProjectWithHouseBlock";

const CustomPropertyWidget = () => {
    const [customProperties, setCustomProperties] = useState<CustomPropertyType[]>();

    useEffect(() => {
        getCustomPropertiesWithQuery("PROJECT").then((customProperties) => setCustomProperties(customProperties));
    }, []);
    return (
        <Grid container my={2}>
            {customProperties &&
                customProperties
                    .filter((p) => !p.disabled)
                    .map((cp, i) => {
                        return (
                            <Grid item xs={6} md={1} key={i}>
                                <Typography sx={columnTitleStyle}>{cp.name}</Typography>
                            </Grid>
                        );
                    })}
        </Grid>
    );
};

export default CustomPropertyWidget;
