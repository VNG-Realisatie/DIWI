import Avatar from "@mui/material/Avatar";
import { Organization, OrganizationUser } from "../api/projectsServices";
import { stringAvatar } from "../utils/stringAvatar";

export const OrganizationUserAvatars = (props: { organizations?: OrganizationUser[] | null }) => {
    if (!props.organizations) {
        return null;
    }

    return (
        <>
            {props.organizations.map((user) => {
                return <Avatar key={user.uuid} {...stringAvatar(`${user.firstName} ${user.lastName}`)} />;
            })}
        </>
    );
};
