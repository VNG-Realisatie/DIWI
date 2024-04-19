import { PropsWithChildren, createContext, useCallback, useContext, useEffect, useState } from "react";
import { HouseBlockWithCustomProperties } from "../types/houseBlockTypes";
import ProjectContext from "./ProjectContext";
import * as houseBlockService from "../api/houseBlockServices";
import { useCustomPropertyDefinitions } from "../hooks/useCustomPropertyDefinitions";

type HouseBlockContextType = {
    houseBlocks: HouseBlockWithCustomProperties[];
    refresh: () => void;
    getEmptyHouseBlock: () => HouseBlockWithCustomProperties;
};

const HouseBlockContext = createContext<HouseBlockContextType | null>(null) as React.Context<HouseBlockContextType>;

export const HouseBlockProvider = ({ children }: PropsWithChildren) => {
    const [houseBlocks, setHouseBlocks] = useState<HouseBlockWithCustomProperties[]>([]);

    const { projectId, selectedProject } = useContext(ProjectContext);
    const { physicalAppearanceCategories, targetGroupCategories } = useCustomPropertyDefinitions();

    const refresh = useCallback(() => {
        projectId &&
            houseBlockService.getProjectHouseBlocksWithCustomProperties(projectId).then((res: HouseBlockWithCustomProperties[]) => {
                setHouseBlocks(res);
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
                value: 0,
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
                    amount: null,
                    value: { value: 0, min: null, max: null },
                    rentalValue: { value: 0, min: null, max: null },
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
            }}
        >
            {children}
        </HouseBlockContext.Provider>
    );
};
export default HouseBlockContext;
