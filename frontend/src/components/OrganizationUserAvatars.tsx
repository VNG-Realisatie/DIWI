import Avatar from "@mui/material/Avatar";
import { Organization } from "../api/projectsServices";
import { stringAvatar } from "../utils/stringAvatar";

export const OrganizationUserAvatars = (props: { organizations?: Organization[] }) => {
    if (!props.organizations) {
        return null;
    }

    return (
        <>
            {props.organizations.map((owner) => {
                return owner.users?.map((user) => {
                    return <Avatar key={user.uuid} {...stringAvatar(`${user.firstName} ${user.lastName}`)} />;
                });
            })}
        </>
    );
};
