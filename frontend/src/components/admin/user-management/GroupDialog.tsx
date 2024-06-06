import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import { t } from "i18next";
import TextInput from "../../project/inputs/TextInput";
import CategoryInput from "../../project/inputs/CategoryInput";
import { Group, User } from "../../../pages/UserManagement";
import { ChangeEvent } from "react";

type GroupDialogProps = {
    open: boolean;
    onClose: () => void;
    newGroup: Group
    setNewGroup: (group: Group) => void;
    handleAddGroup: () => void;
    users: User[];
    title: string;
};

const GroupDialog = ({ open, onClose, newGroup, setNewGroup, handleAddGroup, users, title }: GroupDialogProps) => {
    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>{title}</DialogTitle>
            <DialogContent>
                <Box sx={{ display: "flex", flexDirection: "column", gap: "10px", marginX: "30px" }}>
                    <TextInput
                        readOnly={false}
                        value={newGroup.name}
                        setValue={(event: ChangeEvent<HTMLInputElement>) => {
                            setNewGroup({ ...newGroup, name: event.target.value });
                        }}
                        mandatory={true}
                        title={t("admin.userManagement.groupName")}
                        errorText={t("admin.userManagement.errors.groupName")}
                    />
                    <CategoryInput
                        readOnly={false}
                        mandatory={true}
                        title={t("admin.userManagement.addMember")}
                        options={users ?? []}
                        values={newGroup.users}
                        setValue={(_, newValue) => {
                            const transformedUsers = newValue.map((user: User) => {
                                const { id, email, role, phoneNumber, organization, department, prefixes, contactPerson, ...otherProps } = user;
                                return { uuid: id, ...otherProps };
                            });
                            setNewGroup({ ...newGroup, users: transformedUsers });
                        }}
                        multiple={true}
                        error={t("admin.userManagement.errors.addMember")}
                    />
                </Box>
            </DialogContent>
            <DialogActions>
                <Box sx={{ display: "flex", gap: "10px" }}>
                    <Button
                        onClick={() => {
                            onClose();
                        }}
                        variant="outlined"
                    >
                        {t("generic.cancel")}
                    </Button>
                    <Button onClick={handleAddGroup} variant="contained">
                        {t("generic.save")}
                    </Button>
                </Box>
            </DialogActions>
        </Dialog>
    );
};

export default GroupDialog;
