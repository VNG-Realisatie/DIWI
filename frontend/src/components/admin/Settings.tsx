import {
    Button,
    Checkbox,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    MenuItem,
    Select,
    SelectChangeEvent,
    Stack,
    Switch,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Typography,
} from "@mui/material";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useTranslation } from "react-i18next";
import { ChangeEvent, useEffect, useState } from "react";
import { CustomPropertyType, getCustomProperties } from "../../api/adminSettingServices";
import { objectType, propertyType } from "./constants";

export const rowStyle = {
    p: 1,
    border: "solid 1px #BDBDBD",
};
export const Settings = () => {
    const [openDialog, setOpenDialog] = useState(false);
    const [customProperties, setCustomProperties] = useState<CustomPropertyType[]>();
    const [selectedObjectType, setSelectedObjectType] = useState<string>();
    const [selectedPropertyType, setSelectedPropertyType] = useState<string>();
    const [active, setActive] = useState(false);
    const [name, setName] = useState<string>();
    const headerStyle = {
        color: "#ffffff",
        fontWeight: "600",
        border: "solid 1px #BDBDBD",
    };
    const cellStyle = {
        border: "solid 1px #BDBDBD",
    };

    const { t } = useTranslation();
    const handleSave = () => {
        console.log("");
    };
    useEffect(() => {
        getCustomProperties().then((customProperties) => setCustomProperties(customProperties));
    }, []);
    return (
        <Stack m={2}>
            <Typography fontWeight={600}>{t("admin.settings.title")}</Typography>
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
                                {t("admin.settings.tableHeader.edit")}
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
                                    {row.categories?.map((category) => <>{category}</>)}
                                </TableCell>
                                <TableCell sx={cellStyle} align="right">
                                    <Switch checked={!row.disabled} color="success" />
                                </TableCell>
                                <TableCell sx={cellStyle} align="right">
                                    <Button size="small" variant="contained">
                                        {t("admin.settings.tableHeader.edit")}
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <Stack direction="row" alignItems="center" mt={1} sx={{ cursor: "pointer" }} onClick={() => setOpenDialog(true)}>
                <AddCircleIcon color="info" sx={{ fontSize: "40px" }} />
                {t("admin.settings.add")}
            </Stack>
            <Dialog open={openDialog} onClose={() => setOpenDialog(false)} fullWidth>
                <DialogTitle id="alert-dialog-title"> {t("admin.settings.add")}</DialogTitle>
                <DialogContent>
                    <Stack spacing={2}>
                        <TextField
                            label={t("admin.settings.tableHeader.name")}
                            value={name}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
                        />
                        <Select
                            value={selectedObjectType}
                            label={t("admin.settings.tableHeader.objectType")}
                            onChange={(e: SelectChangeEvent<typeof selectedObjectType>) => setSelectedObjectType(e.target.value)}
                        >
                            {objectType.map((object) => {
                                return <MenuItem value={object}>{object}</MenuItem>;
                            })}
                        </Select>
                        <Select
                            value={selectedPropertyType}
                            label={t("admin.settings.tableHeader.propertyType")}
                            onChange={(e: SelectChangeEvent<typeof selectedPropertyType>) => setSelectedPropertyType(e.target.value)}
                        >
                            {propertyType.map((property) => {
                                return <MenuItem value={property}>{property}</MenuItem>;
                            })}
                        </Select>
                        <Stack>
                            <Typography>
                                {t("admin.settings.tableHeader.active")}
                                <Checkbox checked={active} onChange={() => setActive(!active)} />
                            </Typography>
                        </Stack>
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button variant="contained" color="error" onClick={() => setOpenDialog(false)}>
                        {t("generic.cancel")}
                    </Button>
                    <Button variant="contained" color="success" onClick={handleSave} autoFocus>
                        {t("generic.save")}
                    </Button>
                </DialogActions>
            </Dialog>
        </Stack>
    );
};
