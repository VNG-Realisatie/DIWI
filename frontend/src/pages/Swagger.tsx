import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";
import { API_URI } from "../utils/urls";

export const Swagger = () => {
    return <SwaggerUI url={`${API_URI}/openapi.json`} />;
};
