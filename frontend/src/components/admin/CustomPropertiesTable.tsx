import {
    TableContainer,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    Switch,
    IconButton,
    Tooltip,
    Stack,
    Dialog,
    DialogTitle,
    DialogActions,
    Box,
    Button,
} from "@mui/material";
import { t } from "i18next";
import { CustomPropertyType, deleteCustomProperty, getCustomProperties } from "../../api/adminSettingServices";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { useContext, useState } from "react";
import AlertContext from "../../context/AlertContext";
import { EditPropertyDialog } from "./EditPropertyDialog";

const headerStyle = {
    color: "#ffffff",
    fontWeight: "600",
    border: "solid 1px #BDBDBD",
};
const cellStyle = {
    border: "solid 1px #BDBDBD",
};
type Props = {
    customProperties: CustomPropertyType[];
    setCustomProperties: (customProperties: CustomPropertyType[]) => void;
};
export const CustomPropertiesTable = ({ customProperties, setCustomProperties }: Props) => {
    const [dialogOpen, setDialogOpen] = useState(false);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const { setAlert } = useContext(AlertContext);
    const [deletePropertyInfo, setDeletePropertyInfo] = useState({ name: "", id: "" });
    const [editPropertyId, setEditPropertyId] = useState("");
    const handleDelete = async (id: string, name: string) => {
        setDialogOpen(true);
        setDeletePropertyInfo({ id, name });
    };
    const handleDialogDelete = () =>
        deleteCustomProperty(deletePropertyInfo.id).then((res) => {
            setAlert(t("admin.settings.notifications.successfullyDeleted"), "success");
            setDialogOpen(false);
            getCustomProperties().then((customProperties) => setCustomProperties(customProperties));
        });
    return (
        <>
            <TableContainer sx={{ mt: 2 }}>
                <Table sx={{ minWidth: 650 }}>
                    <TableHead sx={{ backgroundColor: "#738092" }}>
                        <TableRow>
                            <TableCell sx={headerStyle}>{t("admin.settings.tableHeader.name")}</TableCell>
                            <TableCell sx={headerStyle} align="right">
                                {t("admin.settings.tableHeader.propertyType")}
                            </TableCell>
                            <TableCell sx={headerStyle} align="right">
                                {t("admin.settings.tableHeader.objectType")}
                            </TableCell>
                            <TableCell sx={headerStyle} align="right">
                                {t("admin.settings.tableHeader.categories")}
                            </TableCell>
                            <TableCell sx={headerStyle} align="right">
                                {t("admin.settings.tableHeader.status")}
                            </TableCell>
                            <TableCell sx={headerStyle} align="right">
                                {t("admin.settings.tableHeader.actions")}
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {customProperties?.map((row: CustomPropertyType) => (
                            <TableRow key={row.name}>
                                <TableCell component="th" scope="row" sx={cellStyle}>
                                    {row.name}
                                </TableCell>
                                <TableCell sx={cellStyle} align="right">
                                    {row.propertyType}
                                </TableCell>
                                <TableCell sx={cellStyle} align="right">
                                    {row.objectType}
                                </TableCell>
                                <TableCell sx={cellStyle} align="right">
                                    {row.categoryValues?.map((category) => <>{category.name}</>)}
                                </TableCell>
                                <TableCell sx={cellStyle} align="right">
                                    <Switch checked={!row.disabled} color="success" />
                                </TableCell>
                                <TableCell sx={cellStyle}>
                                    <Stack direction="row" spacing={1} justifyContent="flex-end">
                                        <Tooltip title={t("admin.settings.tableHeader.delete")}>
                                            <IconButton
                                                sx={{
                                                    bgcolor: "tomato",
                                                    color: "white",
                                                    "&:hover": {
                                                        bgcolor: "red",
                                                    },
                                                }}
                                                onClick={() => row.id && handleDelete(row.id, row.name)}
                                            >
                                                <DeleteIcon color="inherit" />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title={t("admin.settings.tableHeader.edit")}>
                                            <IconButton
                                                sx={{
                                                    bgcolor: "#31456F",
                                                    color: "white",
                                                    "&:hover": {
                                                        bgcolor: "navy",
                                                    },
                                                }}
                                                onClick={() => {
                                                    row.id && setEditPropertyId(row.id);
                                                    setEditDialogOpen(true);
                                                }}
                                            >
                                                <EditIcon color="inherit" />
                                            </IconButton>
                                        </Tooltip>
                                    </Stack>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)}>
                <DialogTitle>{t("generic.confirmDeletion", { name: deletePropertyInfo.name })}</DialogTitle>
                <DialogActions>
                    <Box sx={{ display: "flex", gap: "10px" }}>
                        <Button onClick={() => setDialogOpen(false)} variant="outlined">
                            {t("generic.no")}
                        </Button>
                        <Button onClick={handleDialogDelete} variant="contained">
                            {t("generic.yes")}
                        </Button>
                    </Box>
                </DialogActions>
            </Dialog>
            <EditPropertyDialog setCustomProperties={setCustomProperties} openDialog={editDialogOpen} setOpenDialog={setEditDialogOpen} id={editPropertyId} />
        </>
    );
};
