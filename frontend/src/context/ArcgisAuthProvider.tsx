import { createContext, useState, ReactNode, useEffect, useCallback } from "react";
import { ArcGISIdentityManager } from "@esri/arcgis-rest-request";
import { useNavigate } from "react-router-dom";
import { configuredExport } from "../Paths";
import useAlert from "../hooks/useAlert";
import { t } from "i18next";
import { getExportDataById } from "../api/exportServices";

interface ArcgisAuthContextProps {
    login: (exportId: string) => void;
    handleRedirect: (exportId: string) => Promise<void>;
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
    const redirectUri = `${window.location.origin}/exchangeimportdata`;

    const login = async (exportId: string) => {
        try {
            const data = await getExportDataById(exportId);
            await ArcGISIdentityManager.beginOAuth2({
                clientId: data?.clientId,
                redirectUri,
                popup: false,
            });
        } catch (error) {
            console.error(error);
        }
    };

    const handleRedirect = useCallback(
        async (exportId: string) => {
            try {
                const data = await getExportDataById(exportId);
                const manager = await ArcGISIdentityManager.completeOAuth2({
                    clientId: data?.clientId,
                    redirectUri,
                    popup: false,
                });
                setToken(manager.token);
                console.log(manager.token);
                sessionStorage.setItem("arcgis_token", manager.token);
                setAlert(t("exchangeData.arcgis.loginSuccess"), "success");

                navigate(configuredExport.toPath({ exportId: exportId }));
            } catch (error) {
                console.error(error);
            }
        },
        [redirectUri, navigate, setAlert],
    );

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const stateParam = urlParams.get("state");
        if (!stateParam) {
            return;
        }
        const state = JSON.parse(decodeURIComponent(stateParam));
        const originalUrl = state.originalUrl;

        const exportIdStart = originalUrl.indexOf("export/") + 7;
        const exportIdEnd = originalUrl.indexOf("?", exportIdStart);
        const exportId = originalUrl.substring(exportIdStart, exportIdEnd !== -1 ? exportIdEnd : undefined);

        if (urlParams.has("code")) {
            handleRedirect(exportId);
        }
    }, [handleRedirect]);

    useEffect(() => {
        const storedToken = sessionStorage.getItem("arcgis_token");
        if (storedToken) {
            setToken(storedToken);
        }
    }, []);

    return <ArcgisAuthContext.Provider value={{ login, handleRedirect, token }}>{children}</ArcgisAuthContext.Provider>;
};
