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
                return owner.users.map((user, id) => {
                    return <Avatar key={id} {...stringAvatar(`${user.firstName[2]} ${user.firstName[3]}`)} />;
                });
            })}
        </>
    );
};
