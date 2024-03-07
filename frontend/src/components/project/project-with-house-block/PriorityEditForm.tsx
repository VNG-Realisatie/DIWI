import { useEffect, useState } from "react";
import { ProjectTableOption, getPriorityList } from "../../../api/projectsTableServices";
import { MenuItem, Select } from "@mui/material";
import { SelectModel } from "../../../api/projectsServices";

type Props = {
    projectPriority: SelectModel | undefined;
    setProjectPriority: (priority: SelectModel | undefined) => void;
};
export const PriorityEditForm = ({ projectPriority, setProjectPriority }: Props) => {
    const [priorityOptionList, setPriorityOptionList] = useState<ProjectTableOption[]>();
    useEffect(() => {
        getPriorityList().then((priorityList) => setPriorityOptionList(priorityList));
    }, []);
    const handlePriorityChange = (event: any) => {
        if (event.target.value) {
            setProjectPriority(event.target.value);
        }
    };

    return (
        <Select fullWidth size="small" id="project-priority-select" value={projectPriority} onChange={handlePriorityChange}>
            {priorityOptionList?.map((ppo) => {
                return (
                    //@ts-ignore
                    <MenuItem key={ppo.id} value={ppo}>
                        {ppo.name}
                    </MenuItem>
                );
            })}
        </Select>
    );
};
