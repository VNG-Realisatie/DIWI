import { useEffect, useState } from "react";
import { TextField, Autocomplete, Checkbox } from "@mui/material";
import { UserGroupAvatars } from "../components/UserGroupAvatars";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank";
import CheckBoxIcon from "@mui/icons-material/CheckBox";
import { UserGroup } from "../api/projectsServices";
import { getUserGroupList } from "../api/projectsTableServices";

const icon = <CheckBoxOutlineBlankIcon />;
const checkedIcon = <CheckBoxIcon />;

type Props = {
    readOnly: boolean;
    userGroup: UserGroup[];
    setUserGroup: (owner: UserGroup[]) => void;
    mandatory?: boolean;
    errorText?: string;
};

const isSingleUserIncluded = true;

const shouldDisplayError = (mandatory: boolean, userGroup: UserGroup[]) => {
    console.log(userGroup);
    return mandatory && userGroup.length === 0;
};

export const UserGroupSelect = ({ readOnly, userGroup, setUserGroup, mandatory, errorText }: Props) => {
    const [ownerOptions, setOwnerOptions] = useState<UserGroup[]>();
    const hasError = shouldDisplayError(mandatory ? true : false, userGroup);

    useEffect(() => {
        getUserGroupList(isSingleUserIncluded).then((groups) => setOwnerOptions(groups));
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
                "& .MuiOutlinedInput-root.MuiInputBase-sizeSmall": {
                    paddingBottom: "3px",
                    paddingTop: "3px",
                },
            }}
            options={ownerOptions ? ownerOptions : []}
            getOptionLabel={(option) => {
                return (
                    option.users
                        ?.map((user) => {
                            const firstInitial = user.firstName ? user.firstName[0] : "";
                            const lastInitial = user.lastName ? user.lastName[0] : "";
                            return `${firstInitial}${lastInitial}`;
                        })
                        .join(" ") || ""
                );
            }}
            onChange={(_, value) => {
                setUserGroup(value);
            }}
            isOptionEqualToValue={(option, value) => option.uuid === value.uuid}
            value={userGroup}
            renderTags={(groups) => <UserGroupAvatars groups={groups} />}
            renderOption={(props, option, { selected }) => (
                <li {...props}>
                    <Checkbox icon={icon} checkedIcon={checkedIcon} checked={selected} />
                    {option.name}
                    <UserGroupAvatars groups={[option]} />
                </li>
            )}
            renderInput={(params) => <TextField {...params} error={hasError} helperText={hasError ? errorText : ""} />}
        />
    );
};

export default UserGroupSelect;
