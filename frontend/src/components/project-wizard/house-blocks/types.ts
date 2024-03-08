import { MutationSelectOptions, OwnershipValueType } from "../../../types/enums";

export type HouseBlock = {
    startDate: string;
    endDate: string;
    projectId: string;
    houseblockId: string;
    houseblockName: string;
    size: {
        value: number | null;
        min: number | null;
        max: number | null;
    };
    programming: boolean | null; //true/false/null

    mutation: {
        mutationKind: MutationSelectOptions[]; // list of enum values: [BOUW, SLOOP, TRANSFORMATIE, SPLITSING]
        grossPlanCapacity: number | null;
        netPlanCapacity: number | null;
        demolition: number | null;
    };

    //eigendom en waarde/ ownership and value
    ownershipValue: [
        {
            type: OwnershipValueType;
            amount: number | null;
            value: {
                value: number | null;
                min: number | null;
                max: number | null;
            };
            rentalValue: {
                value: number | null;
                min: number | null;
                max: number | null;
            };
        },
    ];

    //grond positie
    groundPosition: {
        noPermissionOwner: number | null;
        intentionPermissionOwner: number | null;
        formalPermissionOwner: number | null;
    };

    // fysiek voorkomen
    physicalAppeareance: {
        // total can be between 0 and net plan capacity
        // can not be lower than 0, can be null
        tussenwoning: number | null;
        tweeondereenkap: number | null;
        portiekflat: number | null;
        hoekwoning: number | null;
        vrijstaand: number | null;
        gallerijflat: number | null;
    };

    // huizen type
    houseType: {
        // total can be between 0 and net plan capacity
        // can not be lower than 0, can be null
        meergezinswoning: number | null;
        eengezinswoning: number | null;
    };

    //doel
    purpose: {
        // total can be between 0 and net plan capacity
        // can not be lower than 0, can be null
        regular: number | null;
        youth: number | null;
        student: number | null;
        elderly: number | null;
        largeFamilies: number | null;
        ghz: number | null;
    };
};
