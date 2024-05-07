import { ProjectsChip } from "./ProjectsChip";

type Props = {
    cellValues: {
        id: string;
        name: string;
    }[];
};

export const CategoriesCell = ({ cellValues }: Props) => {
    return <ProjectsChip tagLimit={2} values={cellValues ? cellValues : []} />;
};
