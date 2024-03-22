import { PropsWithChildren, createContext, useCallback, useEffect, useState } from "react";
import { MapBounds, getConfig } from "../api/configServices";

type ConfigContextType = {
    municipalityName: String;
    mapBounds: MapBounds;
};

const defaultNetherlandsMapBounds = {
    corner1: {
        lat: 6569915.4552,
        lng: 369343.7207,
    },
    corner2: {
        lat: 7105586.1494,
        lng: 818793.447,
    },
};

const ConfigContext = createContext<ConfigContextType | null>(null) as React.Context<ConfigContextType>;

export const ConfigProvider = ({ children }: PropsWithChildren) => {
    const [municipalityName, setMunicipalityName] = useState<String>("");
    const [mapBounds, setMapBounds] = useState<MapBounds>(defaultNetherlandsMapBounds);

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
