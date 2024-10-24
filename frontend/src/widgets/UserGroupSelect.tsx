import { useEffect, useState } from "react";
import { TextField, Autocomplete, Checkbox } from "@mui/material";
import { UserGroupAvatars } from "../components/UserGroupAvatars";
import CheckBoxOutlineBlankIcon from "@mui/icons-material/CheckBoxOutlineBlank";
import CheckBoxIcon from "@mui/icons-material/CheckBox";
import { UserGroup } from "../api/projectsServices";
import { getUserGroupList } from "../api/projectsTableServices";
import { useTranslation } from "react-i18next";

const icon = <CheckBoxOutlineBlankIcon />;
const checkedIcon = <CheckBoxIcon />;

type Props = {
    readOnly: boolean;
    userGroup: UserGroup[];
    setUserGroup: (owner: UserGroup[]) => void;
    mandatory: boolean;
    errorText: string;
    placeholder?: string;
    checkIsOwnerValidWithConfidentialityLevel: () => boolean;
};

const isSingleUserIncluded = true;
const projectOwnersOnly = true;

const shouldDisplayError = (mandatory: boolean, userGroup: UserGroup[]) => {
    return mandatory && userGroup.length === 0;
};

export const UserGroupSelect = ({
    readOnly,
    userGroup,
    setUserGroup,
    mandatory,
    errorText,
    placeholder = "",
    checkIsOwnerValidWithConfidentialityLevel,
}: Props) => {
    const [ownerOptions, setOwnerOptions] = useState<UserGroup[]>();
    const hasError = shouldDisplayError(mandatory, userGroup);
    const { t } = useTranslation();

    useEffect(() => {
        getUserGroupList(isSingleUserIncluded, projectOwnersOnly).then((groups) => {
            setOwnerOptions(groups);
        });
    }, []);

    const getErrorText = () => {
        if (hasError) {
            return errorText;
        } else if (!checkIsOwnerValidWithConfidentialityLevel()) {
            return t("createProject.hasMissingRequiredAreas.ownerConfidentialityLevelError");
        }
        return "";
    };

    return (
        <Autocomplete
            multiple
            size="small"
            disabled={readOnly}
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
            renderInput={(params) => (
                <TextField
                    {...params}
                    error={hasError || !checkIsOwnerValidWithConfidentialityLevel()}
                    helperText={getErrorText()}
                    placeholder={hasError ? placeholder : ""}
                />
            )}
        />
    );
};

export default UserGroupSelect;
