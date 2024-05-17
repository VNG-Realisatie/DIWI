import { Autocomplete, Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material";
import { t } from "i18next";
import TextInput from "../../project/inputs/TextInput";

type GroupDialogProps = {
    open: boolean;
    onClose: () => void;
    newGroup: any;
    setNewGroup: (group: any) => void;
    handleAddGroup: () => void;
    users: any[];
};

const GroupDialog = ({ open, onClose, newGroup, setNewGroup, handleAddGroup, users }: GroupDialogProps) => {
    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Voeg een groep toe</DialogTitle>
            <DialogContent>
                <Box sx={{ display: "flex", flexDirection: "column", gap: "10px" }}>
                    <TextInput
                        readOnly={false}
                        value={newGroup.name}
                        setValue={(event: any) => {
                            setNewGroup({ ...newGroup, name: event.target.value });
                        }}
                        mandatory={false}
                        title={t("createProject.informationForm.nameLabel")}
                        errorText={t("createProject.hasMissingRequiredAreas.name")}
                    />
                    {/* Category input should be reused here */}
                    <Autocomplete
                        multiple={true}
                        size="small"
                        disabled={false}
                        sx={{
                            "& .MuiInputBase-input.Mui-disabled": {
                                backgroundColor: "#0000",
                            },
                        }}
                        fullWidth
                        options={users ?? []}
                        getOptionLabel={(option) => {
                            if (option) {
                                return `${option.firstName[0]}${option.lastName[0]}`; // Display initials
                            }

                            return "";
                        }}
                        onAbort={() => {}}
                        value={newGroup.users}
                        onChange={(_, newValue) => {
                            const transformedUsers = newValue.map((user: any) => {
                                const { id, email, role, ...otherProps } = user;
                                return { uuid: id, ...otherProps };
                            });
                            setNewGroup({ ...newGroup, users: transformedUsers });
                        }}
                        filterSelectedOptions
                        renderInput={(params) => (
                            <>
                                <TextField {...params} variant="outlined" />
                            </>
                        )}
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
                    <Button onClick={handleAddGroup} variant="contained">
                        {t("generic.yes")}
                    </Button>
                </Box>
            </DialogActions>
        </Dialog>
    );
};

export default GroupDialog;
