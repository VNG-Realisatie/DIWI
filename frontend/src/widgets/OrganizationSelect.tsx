import { useContext, useEffect, useState } from "react";
import ProjectContext from "../context/ProjectContext";
import { AvatarGroup, TextField, Autocomplete, Checkbox } from "@mui/material";
import { OrganizationUserAvatars } from "../components/OrganizationUserAvatars";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank";
import CheckBoxIcon from "@mui/icons-material/CheckBox";
import { Organization } from "../api/projectsServices";
import { getOrganizationList } from "../api/projectsTableServices";

const icon = <CheckBoxOutlineBlankIcon />;
const checkedIcon = <CheckBoxIcon />;

type Props = {
    projectEditable: boolean;
    owner: Organization[];
    setOwner: (owner: Organization[]) => void;
    isLeader?: boolean;
};

export const OrganizationSelect = ({ projectEditable, owner, setOwner, isLeader }: Props) => {
    const { selectedProject } = useContext(ProjectContext);
    const [ownerOptions, setOwnerOptions] = useState<Organization[]>();

    useEffect(() => {
        getOrganizationList().then((organizations) => setOwnerOptions(organizations));
    }, []);

    if (!projectEditable) {
        if (isLeader) {
            return (
                <AvatarGroup max={3}>
                    <OrganizationUserAvatars organizations={selectedProject?.projectLeaders} />
                </AvatarGroup>
            );
        } else {
            return (
                <AvatarGroup max={3}>
                    <OrganizationUserAvatars organizations={selectedProject?.projectOwners} />
                </AvatarGroup>
            );
        }
    } else {
        // return <TextField disabled size="small" id="organizationName" variant="outlined" />;
        return (
            <Autocomplete
                multiple
                size="small"
                options={ownerOptions ? ownerOptions : []}
                getOptionLabel={(option) => option.name}
                isOptionEqualToValue={(option, value) => option.uuid === value.uuid}
                value={owner}
                onChange={(_: any, newValue: Organization[]) => setOwner(newValue)}
                renderOption={(props, option, { selected }) => (
                    <li {...props}>
                        <Checkbox icon={icon} checkedIcon={checkedIcon} style={{ marginRight: 8 }} checked={selected} />
                        {option.name}
                        <AvatarGroup max={3}>
                            <OrganizationUserAvatars organizations={[option]} />
                        </AvatarGroup>
                    </li>
                )}
                renderInput={(params) => <TextField {...params} />}
            />
        );
    }
};
