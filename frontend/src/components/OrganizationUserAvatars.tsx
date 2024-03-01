import Avatar from "@mui/material/Avatar";
import { Organization } from "../api/projectsServices";
import { stringAvatar } from "../utils/stringAvatar";

export const OrganizationUserAvatars = (props: { organizations?: Organization[] | null }) => {
    if (!props.organizations) {
        return null;
    }

    return (
        <>
            {props.organizations.map((owner) => {
                if (owner.users && owner.users.length > 0) {
                    return owner.users.map((user) => <Avatar key={user.uuid} {...stringAvatar(`${user.firstName} ${user.lastName}`)} />);
                }
                return null;
            })}
        </>
    );
};
