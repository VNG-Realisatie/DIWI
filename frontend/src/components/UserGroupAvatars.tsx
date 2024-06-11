import Avatar from "@mui/material/Avatar";
import { UserGroup } from "../api/projectsServices";
import { stringAvatar } from "../utils/stringAvatar";
import { AvatarGroup } from "@mui/material";

export const UserGroupAvatars = (props: { groups?: UserGroup[] | null }) => {
    if (!props.groups) {
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
            {props.groups.map((owner) => {
                return owner.users?.map((user) => {
                    const isUserDuplicated = users.has(user.uuid);
                    users.add(user.uuid);
                    return isUserDuplicated ? null : <Avatar key={user.uuid} {...stringAvatar(`${user.firstName} ${user.lastName}`)} />;
                });
            })}
        </AvatarGroup>
    );
};
