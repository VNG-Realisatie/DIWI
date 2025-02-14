import { createContext, useState, ReactNode, useEffect, useCallback } from "react";
import { ArcGISIdentityManager } from "@esri/arcgis-rest-request";
import { useNavigate } from "react-router-dom";
import { configuredExport } from "../Paths";
import useAlert from "../hooks/useAlert";
import { t } from "i18next";

interface ArcgisAuthContextProps {
    login: () => void;
    handleRedirect: (exportId: string | undefined) => Promise<void>;
    token: string | null;
}

interface ArcgisAuthProviderProps {
    children: ReactNode;
}

export const ArcgisAuthContext = createContext<ArcgisAuthContextProps | undefined>(undefined);

export const ArcgisAuthProvider = ({ children }: ArcgisAuthProviderProps) => {
    const [token, setToken] = useState<string | null>(null);
    const navigate = useNavigate();
    const { setAlert } = useAlert();

    const clientId = import.meta.env.VITE_REACT_APP_ARCGIS_CLIENT_ID;
    const redirectUri = `${window.location.origin}/exchangeimportdata`;

    const login = async () => {
        try {
            await ArcGISIdentityManager.beginOAuth2({
                clientId,
                redirectUri,
                popup: false,
            });
        } catch (error) {
            console.error(error);
        }
    };

    const handleRedirect = useCallback(
        async (exportId: string | undefined) => {
            const manager = await ArcGISIdentityManager.completeOAuth2({
                clientId,
                redirectUri,
                popup: false,
            });
            setToken(manager.token);
            sessionStorage.setItem("arcgis_token", manager.token);
            setAlert(t("exchangeData.arcgis.loginSuccess"), "success");

            if (exportId) {
                navigate(configuredExport.toPath({ exportId }));
            }
        },
        [clientId, redirectUri, navigate, setAlert],
    );

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const stateParam = urlParams.get("state");
        let exportId;
        if (stateParam) {
            const stateObj = JSON.parse(stateParam);
            const originalUrl = stateObj.originalUrl;

            const startIndex = originalUrl.indexOf("/export/") + "/export/".length;
            const endIndex = originalUrl.indexOf("?", startIndex);
            exportId = originalUrl.substring(startIndex, endIndex);
        }
        if (urlParams.has("code")) {
            handleRedirect(exportId);
        } else {
            const storedToken = sessionStorage.getItem("arcgis_token");
            if (storedToken) {
                setToken(storedToken);
            }
        }
    }, [handleRedirect]);

    return <ArcgisAuthContext.Provider value={{ login, handleRedirect, token }}>{children}</ArcgisAuthContext.Provider>;
};
