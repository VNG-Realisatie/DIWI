import Avatar from "@mui/material/Avatar";
import { UserGroup } from "../api/projectsServices";
import { stringAvatar } from "../utils/stringAvatar";

export const UserGroupAvatars = (props: { groups?: UserGroup[] | null }) => {
    if (!props.groups) {
        return null;
    }

    return (
        <>
            {props.groups.map((owner) => {
                return owner.users?.map((user) => {
                    return <Avatar key={user.uuid} {...stringAvatar(`${user.firstName} ${user.lastName}`)} />;
                });
            })}
        </>
    );
};
