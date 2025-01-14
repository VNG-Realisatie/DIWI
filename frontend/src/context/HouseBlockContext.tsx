import { PropsWithChildren, createContext, useCallback, useContext, useEffect, useState } from "react";
import { HouseBlockWithCustomProperties } from "../types/houseBlockTypes";
import ProjectContext from "./ProjectContext";
import * as houseBlockService from "../api/houseBlockServices";
import { useCustomPropertyDefinitions } from "../hooks/useCustomPropertyDefinitions";
import { Property } from "../api/adminSettingServices";
import { useCustomPropertyStore } from "../hooks/useCustomPropertyStore";

type HouseBlockContextType = {
    houseBlocks: HouseBlockWithCustomProperties[];
    refresh: () => void;
    getEmptyHouseBlock: () => HouseBlockWithCustomProperties;
    nonFixedCustomDefinitions: Property[];
};

// eslint-disable-next-line react-refresh/only-export-components
export const getEmptyHouseBlock: () => HouseBlockWithCustomProperties = () => ({
    projectId: "",
    startDate: null,
    endDate: null,
    houseblockName: "",
    size: {
        value: null,
        min: null,
        max: null,
    },
    programming: null,
    mutation: {
        kind: null,
        amount: null,
    },
    ownershipValue: [
        {
            type: "KOOPWONING",
            amount: 0,
            value: { value: null, min: null, max: null },
            rentalValue: { value: null, min: null, max: null },
        },
    ],
    groundPosition: {
        noPermissionOwner: null,
        intentionPermissionOwner: null,
        formalPermissionOwner: null,
    },
    physicalAppearance: [],
    houseType: {
        meergezinswoning: null,
        eengezinswoning: null,
    },
    targetGroup: [],
    customProperties: [],
});

const defaultHouseBlockContext: HouseBlockContextType = {
    houseBlocks: [],
    refresh: () => {},
    getEmptyHouseBlock: getEmptyHouseBlock,
    nonFixedCustomDefinitions: [],
};

const HouseBlockContext = createContext<HouseBlockContextType>(defaultHouseBlockContext);

export const HouseBlockProvider = ({ children }: PropsWithChildren) => {
    const [houseBlocks, setHouseBlocks] = useState<HouseBlockWithCustomProperties[]>([]);

    const { projectId, selectedProject } = useContext(ProjectContext);
    const { physicalAppearanceCategories, targetGroupCategories } = useCustomPropertyDefinitions();
    const { houseBlockCustomProperties } = useCustomPropertyStore();

    const nonFixedCustomDefinitions = houseBlockCustomProperties.filter((property) => !property.disabled && property.type !== "FIXED");

    const refresh = useCallback(() => {
        if (!projectId) {
            setHouseBlocks([]);
            return;
        }
        houseBlockService
            .getProjectHouseBlocksWithCustomProperties(projectId)
            .then((res: HouseBlockWithCustomProperties[]) => {
                if (!res || res.length === 0) {
                    setHouseBlocks([]);
                } else {
                    setHouseBlocks(res);
                }
            })
            .catch(() => {
                setHouseBlocks([]);
            });
    }, [projectId]);

    useEffect(() => {
        refresh();
    }, [projectId, refresh]);

    const getEmptyHouseBlock = (): HouseBlockWithCustomProperties => {
        return {
            projectId: projectId,
            startDate: selectedProject?.startDate ?? null,
            endDate: selectedProject?.endDate ?? null,
            houseblockName: "",
            size: {
                value: null,
                min: null,
                max: null,
            },
            programming: null,
            mutation: {
                kind: null,
                amount: null,
            },
            ownershipValue: [
                {
                    type: "KOOPWONING",
                    amount: 0,
                    value: { value: null, min: null, max: null },
                    rentalValue: { value: null, min: null, max: null },
                },
            ],
            groundPosition: {
                noPermissionOwner: null,
                intentionPermissionOwner: null,
                formalPermissionOwner: null,
            },
            physicalAppearance: physicalAppearanceCategories?.map((cat) => ({ id: cat.id, amount: 0 })) ?? [],
            houseType: {
                meergezinswoning: null,
                eengezinswoning: null,
            },
            targetGroup: targetGroupCategories?.map((cat) => ({ id: cat.id, amount: 0 })) ?? [],
            customProperties: [],
        };
    };
    return (
        <HouseBlockContext.Provider
            value={{
                houseBlocks,
                refresh,
                getEmptyHouseBlock,
                nonFixedCustomDefinitions,
            }}
        >
            {children}
        </HouseBlockContext.Provider>
    );
};

export default HouseBlockContext;
