import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid } from "@mui/material";
import { t } from "i18next";
import TextInput from "../../project/inputs/TextInput";
import { User } from "../../../pages/UserManagement";
import CategoryInput from "../../project/inputs/CategoryInput";
import { roleTypeOptions } from "../../../types/enums";
import { ChangeEvent } from "react";
import { validateEmail } from "../../../utils/emailValidation";

type UserDialogProps = {
    open: boolean;
    onClose: () => void;
    newUser: User;
    setNewUser: (user: User) => void;
    handleAddUser: () => void;
    title?: string;
};

const UserDialog = ({ open, onClose, newUser, setNewUser, handleAddUser, title }: UserDialogProps) => {
    const isFormInvalid = !newUser.firstName || !newUser.lastName || !newUser.role || !newUser.email || !validateEmail(newUser.email);
    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
            <DialogTitle>{title}</DialogTitle>
            <DialogContent>
                <Box sx={{ display: "flex", flexDirection: "column", gap: "10px", marginX: "30px" }}>
                    <TextInput
                        readOnly={false}
                        value={newUser.firstName ?? ""}
                        setValue={(event: ChangeEvent<HTMLInputElement>) => {
                            setNewUser({ ...newUser, firstName: event.target.value });
                        }}
                        mandatory={true}
                        title={t("admin.userManagement.tableHeader.name.firstName")}
                        errorText={t("admin.userManagement.errors.firstName")}
                    />
                    <TextInput
                        readOnly={false}
                        value={newUser.lastName ?? ""}
                        setValue={(event: ChangeEvent<HTMLInputElement>) => {
                            setNewUser({ ...newUser, lastName: event.target.value });
                        }}
                        mandatory={true}
                        title={t("admin.userManagement.tableHeader.name.lastName")}
                        errorText={t("admin.userManagement.errors.lastName")}
                    />
                    <Grid container spacing={2}>
                        <Grid item xs={4}>
                            <TextInput
                                readOnly={false}
                                value={newUser.phoneNumber ?? ""}
                                setValue={(event: ChangeEvent<HTMLInputElement>) => {
                                    setNewUser({ ...newUser, phoneNumber: event.target.value });
                                }}
                                mandatory={false}
                                title={t("admin.userManagement.tableHeader.phone")}
                                errorText={t("admin.userManagement.errors.phone")}
                                type="tel"
                            />
                        </Grid>
                        <Grid item xs={8}>
                            <TextInput
                                readOnly={false}
                                value={newUser.email ?? ""}
                                setValue={(event: ChangeEvent<HTMLInputElement>) => {
                                    setNewUser({ ...newUser, email: event.target.value });
                                }}
                                mandatory={true}
                                title={t("admin.userManagement.tableHeader.email")}
                                errorText={t("admin.userManagement.errors.email")}
                                type="email"
                            />
                        </Grid>
                    </Grid>
                    <TextInput
                        readOnly={false}
                        value={newUser.organization ?? ""}
                        setValue={(event: ChangeEvent<HTMLInputElement>) => {
                            setNewUser({ ...newUser, organization: event.target.value });
                        }}
                        mandatory={false}
                        title={t("admin.userManagement.tableHeader.organization")}
                        errorText={t("admin.userManagement.errors.organization")}
                    />
                    <TextInput
                        readOnly={false}
                        value={newUser.department ?? ""}
                        setValue={(event: ChangeEvent<HTMLInputElement>) => {
                            setNewUser({ ...newUser, department: event.target.value });
                        }}
                        mandatory={false}
                        title={t("admin.userManagement.tableHeader.department")}
                        errorText={t("admin.userManagement.errors.department")}
                    />
                    <CategoryInput
                        readOnly={false}
                        mandatory={true}
                        title={t("admin.userManagement.tableHeader.role")}
                        options={roleTypeOptions.map((role) => ({ id: role, name: t(`admin.userManagement.roles.${role}`) }))}
                        values={newUser?.role ? { id: newUser.role, name: t(`admin.userManagement.roles.${newUser.role}`) } : null}
                        setValue={(_, newValue) => {
                            if (newValue && newValue.id) {
                                setNewUser({
                                    ...newUser,
                                    role: newValue.id,
                                });
                            }
                        }}
                        multiple={false}
                        error={t("admin.userManagement.errors.role")}
                    />
                    {/* <TextInput
                        readOnly={false}
                        value={newUser.contactPerson ?? ""}
                        setValue={(event: any) => {
                            setNewUser({ ...newUser, contactPerson: event.target.value });
                        }}
                        mandatory={false}
                        title={t("admin.userManagement.tableHeader.contactPerson")}
                    />
                    <TextInput
                        readOnly={false}
                        value={newUser.prefixes ?? ""}
                        setValue={(event: any) => {
                            setNewUser({ ...newUser, prefixes: event.target.value });
                        }}
                        mandatory={false}
                        title={t("admin.userManagement.tableHeader.prefixes")}
                    /> */}
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
                    <Button onClick={handleAddUser} variant="contained" disabled={isFormInvalid}>
                        {t("generic.save")}
                    </Button>
                </Box>
            </DialogActions>
        </Dialog>
    );
};

export default UserDialog;
