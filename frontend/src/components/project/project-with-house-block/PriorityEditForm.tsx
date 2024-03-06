import { useEffect, useState } from "react";
import { ProjectTableOption, getPriorityList } from "../../../api/projectsTableServices";
import { MenuItem, Select, SelectChangeEvent } from "@mui/material";

type Props = {
    projectPriority: string | undefined;
    setProjectPriority: (priority: string) => void;
};
export const PriorityEditForm = ({ projectPriority, setProjectPriority }: Props) => {
    const [priorityOptionList, setPriorityOptionList] = useState<ProjectTableOption[]>();
    useEffect(() => {
        getPriorityList().then((priorityList) => setPriorityOptionList(priorityList));
    }, []);
    const handlePriorityChange = (event: SelectChangeEvent<typeof projectPriority>) => {
        if (event.target.value) {
            setProjectPriority(event.target.value);
        }
    };

    return (
        <Select fullWidth size="small" id="project-priority-select" value={projectPriority} onChange={handlePriorityChange}>
            {priorityOptionList?.map((ppo) => {
                return (
                    <MenuItem key={ppo.id} value={ppo.id}>
                        {ppo.name}
                    </MenuItem>
                );
            })}
        </Select>
    );
};
