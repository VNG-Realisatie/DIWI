import { useEffect, useState } from "react";
import { AvatarGroup, TextField, Autocomplete, Checkbox } from "@mui/material";
import { UserGroupAvatars } from "../components/UserGroupAvatars";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank";
import CheckBoxIcon from "@mui/icons-material/CheckBox";
import { UserGroup } from "../api/projectsServices";
import { getOrganizationList as getUserGroupList } from "../api/projectsTableServices";

const icon = <CheckBoxOutlineBlankIcon />;
const checkedIcon = <CheckBoxIcon />;

type Props = {
    readOnly: boolean;
    userGroup: UserGroup[];
    setUserGroup: (owner: UserGroup[]) => void;
};

export const UserGroupSelect = ({ readOnly, userGroup, setUserGroup }: Props) => {
    const [ownerOptions, setOwnerOptions] = useState<UserGroup[]>();

    useEffect(() => {
        getUserGroupList().then((groups) => setOwnerOptions(groups));
    }, []);

    return (
        <Autocomplete
            multiple
            size="small"
            disabled={readOnly}
            sx={{
                "& .MuiInputBase-input.Mui-disabled": {
                    backgroundColor: "#0000", // set 0 opacity when disabled
                },
            }}
            options={ownerOptions ? ownerOptions : []}
            getOptionLabel={(option) => option.name}
            isOptionEqualToValue={(option, value) => option.uuid === value.uuid}
            value={userGroup}
            onChange={(_: any, newValue: UserGroup[]) => setUserGroup(newValue)}
            renderOption={(props, option, { selected }) => (
                <li {...props}>
                    <Checkbox icon={icon} checkedIcon={checkedIcon} style={{ marginRight: 8 }} checked={selected} />
                    {option.name}
                    <AvatarGroup max={3}>
                        <UserGroupAvatars groups={[option]} />
                    </AvatarGroup>
                </li>
            )}
            renderInput={(params) => <TextField {...params} />}
        />
    );
};

export default UserGroupSelect;
