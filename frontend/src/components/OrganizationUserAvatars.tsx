import Avatar from "@mui/material/Avatar";
import { Organization } from "../api/projectsServices";
import { stringAvatar } from "../utils/stringAvatar";
import { AvatarGroup } from "@mui/material";

export const OrganizationUserAvatars = (props: { organizations?: Organization[] | null }) => {
    if (!props.organizations) {
        return null;
    }

    const users = new Set<string | undefined>();

    return (
        <AvatarGroup
            max={6}
            sx={{
                "& .MuiAvatar-root": { width: 30, height: 30, fontSize: 14 },
            }}
        >
            {props.organizations.map((owner) => {
                return owner.users?.map((user) => {
                    let isUserDuplicated = users.has(user.uuid);
                    users.add(user.uuid);
                    return isUserDuplicated ? null : <Avatar key={user.uuid} {...stringAvatar(`${user.firstName} ${user.lastName}`)} />;
                });
            })}
        </AvatarGroup>
    );
};
