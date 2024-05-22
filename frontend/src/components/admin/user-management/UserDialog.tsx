import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import { t } from "i18next";
import TextInput from "../../project/inputs/TextInput";
import { User } from "../../../pages/UserManagement";

type UserDialogProps = {
    open: boolean;
    onClose: () => void;
    newUser: User;
    setNewUser: (user: any) => void;
    handleAddUser: any;
};

const UserDialog = ({ open, onClose, newUser, setNewUser, handleAddUser }: UserDialogProps) => {
    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
            <DialogTitle>{t("admin.userManagement.addUser")}</DialogTitle>
            <DialogContent>
                <Box sx={{ display: "flex", flexDirection: "column", gap: "10px" }}>
                    <TextInput
                        readOnly={false}
                        value={newUser.firstName ?? ""}
                        setValue={(event: any) => {
                            setNewUser({ ...newUser, firstName: event.target.value });
                        }}
                        mandatory={true}
                        title={t("admin.userManagement.tableHeader.name.firstName")}
                        errorText={t("admin.userManagement.errors.firstName")}
                    />
                    <TextInput
                        readOnly={false}
                        value={newUser.lastName ?? ""}
                        setValue={(event: any) => {
                            setNewUser({ ...newUser, lastName: event.target.value });
                        }}
                        mandatory={true}
                        title={t("admin.userManagement.tableHeader.name.lastName")}
                        errorText={t("admin.userManagement.errors.lastName")}
                    />
                    <TextInput
                        readOnly={false}
                        value={newUser.email ?? ""}
                        setValue={(event: any) => {
                            setNewUser({ ...newUser, email: event.target.value });
                        }}
                        mandatory={true}
                        title={t("admin.userManagement.tableHeader.email")}
                        errorText={t("admin.userManagement.errors.email")}
                    />
                    {/* should use category input */}
                    <TextInput
                        readOnly={false}
                        value={newUser.role ?? ""}
                        setValue={(event: any) => {
                            setNewUser({ ...newUser, role: event.target.value });
                        }}
                        mandatory={true}
                        title={t("admin.userManagement.tableHeader.role")}
                        errorText={t("admin.userManagement.errors.role")}
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
                        {t("generic.no")}
                    </Button>
                    <Button onClick={handleAddUser} variant="contained">
                        {t("generic.yes")}
                    </Button>
                </Box>
            </DialogActions>
        </Dialog>
    );
};

export default UserDialog;
