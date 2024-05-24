import { useEffect, useState } from "react";
import { TextField, Autocomplete, Checkbox } from "@mui/material";
import { OrganizationUserAvatars } from "../components/OrganizationUserAvatars";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank";
import CheckBoxIcon from "@mui/icons-material/CheckBox";
import { Organization } from "../api/projectsServices";
import { getOrganizationList } from "../api/projectsTableServices";

const icon = <CheckBoxOutlineBlankIcon />;
const checkedIcon = <CheckBoxIcon />;

type Props = {
    readOnly: boolean;
    userGroup: Organization[];
    setUserGroup: (owner: Organization[]) => void;
};

export const OrganizationSelect = ({ readOnly, userGroup, setUserGroup }: Props) => {
    const [ownerOptions, setOwnerOptions] = useState<Organization[]>();

    useEffect(() => {
        getOrganizationList().then((organizations) => setOwnerOptions(organizations));
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
                "& .MuiInputBase-root": {
                    paddingBottom: "3px !important",
                    paddingTop: "3px !important",
                },
            }}
            options={ownerOptions ? ownerOptions : []}
            getOptionLabel={(option) => option.name}
            isOptionEqualToValue={(option, value) => option.uuid === value.uuid}
            value={userGroup}
            renderTags={(values) => <OrganizationUserAvatars organizations={values} />}
            onChange={(_: any, newValue: Organization[]) => setUserGroup(newValue)}
            renderOption={(props, option, { selected }) => (
                <li {...props}>
                    <Checkbox icon={icon} checkedIcon={checkedIcon} checked={selected} />
                    {option.name}
                    <OrganizationUserAvatars organizations={[option]} />
                </li>
            )}
            renderInput={(params) => <TextField {...params} />}
        />
    );
};
