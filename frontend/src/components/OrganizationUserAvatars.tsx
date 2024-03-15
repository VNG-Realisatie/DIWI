import Avatar from "@mui/material/Avatar";
import { Organization, OrganizationUser } from "../api/projectsServices";
import { stringAvatar } from "../utils/stringAvatar";

export const OrganizationUserAvatars = (props: { organizations?: Organization[] | null }) => {
    if (!props.organizations) {
        return null;
    }
    console.log("organizations", props.organizations);
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
