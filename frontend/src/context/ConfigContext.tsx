import { PropsWithChildren, createContext, useCallback, useEffect, useState } from "react";
import { MapBounds, getConfig } from "../api/configServices";

type ConfigContextType = {
    municipalityName: String;
    mapBounds: MapBounds | null;
};

const ConfigContext = createContext<ConfigContextType | null>(null) as React.Context<ConfigContextType>;

export const ConfigProvider = ({ children }: PropsWithChildren) => {
    const [municipalityName, setMunicipalityName] = useState<String>("");
    const [mapBounds, setMapBounds] = useState<MapBounds | null>(null);

    const updateConfigSettings = useCallback(() => {
        getConfig()
            .then((config) => {
                setMapBounds(config.defaultMapBounds);
                setMunicipalityName(config.municipalityName);
            })
            .catch((e) => {
                console.log(e);
            });
    }, []);

    useEffect(() => {
        updateConfigSettings();
    }, [updateConfigSettings]);

    return (
        <ConfigContext.Provider
            value={{
                municipalityName,
                mapBounds,
            }}
        >
            {children}
        </ConfigContext.Provider>
    );
};
export default ConfigContext;
