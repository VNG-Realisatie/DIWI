import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";
import { API_URI } from "../utils/urls";
import ActionNotAllowed from "./ActionNotAllowed";
import { t } from "i18next";
import { useContext } from "react";
import UserContext from "../context/UserContext";

export const Swagger = () => {
    const { allowedActions } = useContext(UserContext);

    if (!allowedActions.includes("VIEW_API")) {
        return <ActionNotAllowed errorMessage={t("generic.cantAccessPage")} />;
    }

    return <SwaggerUI url={`${API_URI}/openapi.json`} />;
};
