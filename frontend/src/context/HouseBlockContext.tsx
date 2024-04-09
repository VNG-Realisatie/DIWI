import { PropsWithChildren, createContext, useContext, useEffect, useState } from "react";
import { HouseBlock } from "../types/houseBlockTypes";
import ProjectContext from "./ProjectContext";
import { getProjectHouseBlocks } from "../api/projectsServices";
import * as projectServices from "../api/projectsServices";
import useAlert from "../hooks/useAlert";
import { useTranslation } from "react-i18next";
import * as customPropServices from "../api/customPropServices";
import { CustomPropertyValue } from "../api/customPropServices";

type CustomPropertyValueHelper = {
    houseBlockId: string;
    customPropertyValues: CustomPropertyValue[];
};
type HouseBlockContextType = {
    houseBlocks: HouseBlock[];
    updateHouseBlock: (houseBlock: HouseBlock) => void;
    addHouseBlock: (houseBlock: HouseBlock) => void;
    // saveHouseBlocks: () => void;
    createHouseBlock: (houseBlock: HouseBlock) => void;
    updateCustomPropertyValues: (houseBlockId: string, customPropertyValues: CustomPropertyValue[]) => void;
    getCustomPropertyValues: (houseBlockId: string) => CustomPropertyValue[];
    getEmptyHouseBlock: () => HouseBlock;
};

const HouseBlockContext = createContext<HouseBlockContextType | null>(null) as React.Context<HouseBlockContextType>;

export const HouseBlockProvider = ({ children }: PropsWithChildren) => {
    const [houseBlocks, setHouseBlocks] = useState<HouseBlock[]>([]);
    const [customPropertiesValues, setCustomPropertiesValues] = useState<CustomPropertyValueHelper[]>([]);
    const { setAlert } = useAlert();
    const { t } = useTranslation();

    const { projectId, selectedProject } = useContext(ProjectContext);
    useEffect(() => {
        projectId &&
            getProjectHouseBlocks(projectId).then((res: HouseBlock[]) => {
                setHouseBlocks(res);
            });
    }, [projectId]);

    const updateCustomPropertyValues = (houseBlockId: string, customPropertyValues: CustomPropertyValue[]) => {
        const newCustomPropertiesValues = customPropertiesValues.filter((cpv) => cpv.houseBlockId !== houseBlockId);
        setCustomPropertiesValues([...newCustomPropertiesValues, { houseBlockId, customPropertyValues }]);
    };

    const updateHouseBlock = (houseBlock: HouseBlock) => {
        if (
            !houseBlock.houseblockName ||
            !houseBlock.startDate ||
            !houseBlock.endDate ||
            houseBlock.ownershipValue.some((owner) => owner.amount === null || isNaN(owner.amount))
        ) {
            setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
            return;
        }
        const updatedHouseBlock = houseBlocks.filter((hb) => hb.houseblockId !== houseBlock.houseblockId);
        projectServices
            .updateHouseBlock(houseBlock)
            .then((res) => {
                const customVal = customPropertiesValues.find((cpv) => cpv.houseBlockId === res.houseblockId)?.customPropertyValues ?? [];
                Promise.all(
                    customVal.map(
                        (cv) =>
                            res.houseblockId &&
                            customPropServices
                                .putBlockCustomPropertyValues(res.houseblockId, cv)
                                .then((_) => false)
                                .catch((_) => true),
                    ),
                ).then((res) => {
                    if (res.includes(true)) {
                        setAlert(t("generic.failedToSave"), "error");
                        return;
                    } else {
                        setAlert(t("generic.saved"), "success");
                    }
                });
                setHouseBlocks([...updatedHouseBlock, res]);
            })
            .catch(() => setAlert(t("generic.failedToSave"), "error"));
    };

    useEffect(() => {
        const result: CustomPropertyValueHelper[] = [];
        houseBlocks.forEach((hb) => {
            if (hb && hb.houseblockId !== undefined) {
                customPropServices.getBlockCustomPropertyValues(hb.houseblockId).then((cpv) =>
                    result.push({
                        //@ts-ignore
                        houseBlockId: hb.houseblockId,
                        customPropertyValues: cpv,
                    }),
                );
            }
        });
        setCustomPropertiesValues(result);
    }, [houseBlocks]);

    const addHouseBlock = (houseBlock: HouseBlock) => {
        if (
            !houseBlock.houseblockName ||
            !houseBlock.startDate ||
            !houseBlock.endDate ||
            houseBlock.ownershipValue.some((owner) => owner.amount === null || isNaN(owner.amount))
        ) {
            setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
            return;
        }
        projectServices
            .addHouseBlock(houseBlock)
            .then((res) => {
                setHouseBlocks([...houseBlocks, res]);
                const customVal = customPropertiesValues.find((cpv) => cpv.houseBlockId === res.houseblockId)?.customPropertyValues ?? [];
                Promise.all(
                    customVal.map(
                        (cv) =>
                            res.houseblockId &&
                            customPropServices
                                .putCustomPropertyValues(res.houseblockId, cv)
                                .then((_) => false)
                                .catch((_) => true),
                    ),
                ).then((res) => {
                    if (res.includes(true)) {
                        setAlert(t("generic.failedToSave"), "error");
                        return;
                    } else {
                        setAlert(t("generic.saved"), "success");
                    }
                });
            })
            .catch(() => setAlert(t("generic.failedToSave"), "error"));
    };

    const createHouseBlock = (houseBlock: HouseBlock) => {
        if (
            !houseBlock.houseblockName ||
            !houseBlock.startDate ||
            !houseBlock.endDate ||
            houseBlock.ownershipValue.some((owner) => owner.amount === null || isNaN(owner.amount))
        ) {
            setAlert(t("createProject.hasMissingRequiredAreas.hasmissingProperty"), "warning");
            return;
        }
        setHouseBlocks([...houseBlocks, houseBlock]);
    };

    const getCustomPropertyValues = (houseBlockId: string | undefined) => {
        return customPropertiesValues.find((cpv) => cpv.houseBlockId === houseBlockId)?.customPropertyValues ?? [];
    };

    // const saveHouseBlocks = () => {
    //     const newHouseBlocks = houseBlocks.filter((hb) => !hb.houseblockId);
    //     newHouseBlocks.forEach((hb) => {
    //         api.addHouseBlock(hb)
    //             .then((res) => {
    //                 setAlert(t("generic.saved"), "success");
    //                 setHouseBlocks([...houseBlocks, res]);
    //             })
    //             .catch(() => setAlert(t("generic.failedToSave"), "error"));
    //     });

    //     const existingHouseBlocks = houseBlocks.filter((hb) => hb.houseblockId);
    //     existingHouseBlocks.forEach((hb) => {
    //         api.updateHouseBlock(hb)
    //             .then((res) => {
    //                 setAlert(t("generic.updated"), "success");
    //                 setHouseBlocks([...houseBlocks, res]);
    //             })
    //             .catch(() => setAlert(t("generic.failedToUpdate"), "error"));
    //     });
    // };

    const getEmptyHouseBlock = (): HouseBlock => {
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
                mutationKind: [],
                grossPlanCapacity: 0,
                netPlanCapacity: 0,
                demolition: 0,
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
            physicalAppearance: {
                tussenwoning: null,
                tweeondereenkap: null,
                portiekflat: null,
                hoekwoning: null,
                vrijstaand: null,
                gallerijflat: null,
            },
            houseType: {
                meergezinswoning: null,
                eengezinswoning: null,
            },
            purpose: {
                regular: null,
                youth: null,
                student: null,
                elderly: null,
                largeFamilies: null,
                ghz: null,
            },
        };
    };

    return (
        <HouseBlockContext.Provider
            value={{
                houseBlocks,
                updateHouseBlock,
                addHouseBlock,
                // saveHouseBlocks,
                createHouseBlock,
                updateCustomPropertyValues,
                getCustomPropertyValues,
                getEmptyHouseBlock,
            }}
        >
            {children}
        </HouseBlockContext.Provider>
    );
};
export default HouseBlockContext;
