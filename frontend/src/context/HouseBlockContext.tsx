import { PropsWithChildren, createContext, useContext, useEffect, useState } from "react";
import { HouseBlock } from "../components/project-wizard/house-blocks/types";
import ProjectContext from "./ProjectContext";
import { getProjectHouseBlocks } from "../api/projectsServices";

type HouseBlockContextType = {
    setHouseBlocks: (houseBlocks: HouseBlock[]) => void;
    houseBlocks: HouseBlock[];
};

const HouseBlockContext = createContext<HouseBlockContextType | null>(null) as React.Context<HouseBlockContextType>;

export const HouseBlockProvider = ({ children }: PropsWithChildren) => {
    const [houseBlocks, setHouseBlocks] = useState<HouseBlock[]>([]);

    const { id } = useContext(ProjectContext);

    useEffect(() => {
        id && getProjectHouseBlocks(id).then((res) => setHouseBlocks(res));
    }, [id]);
    return (
        <HouseBlockContext.Provider
            value={{
                setHouseBlocks,
                houseBlocks,
            }}
        >
            {children}
        </HouseBlockContext.Provider>
    );
};
export default HouseBlockContext;
