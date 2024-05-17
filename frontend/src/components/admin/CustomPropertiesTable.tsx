import { Box, Chip, Dialog, DialogActions, DialogTitle, Button, Tooltip, Stack } from "@mui/material";
import { DataGrid, GridColDef, GridActionsCellItem, getGridStringOperators } from "@mui/x-data-grid";
import { useTranslation } from "react-i18next";
import { Property, deleteCustomProperty, getCustomProperties, CategoryType, OrdinalCategoryType } from "../../api/adminSettingServices";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { useContext, useState } from "react";
import AlertContext from "../../context/AlertContext";
import PropertyDialog from "./PropertyDialog";

type Props = {
    customProperties: Property[];
    setCustomProperties: (customProperties: Property[]) => void;
};

const headerStyle = {
    color: "#ffffff",
    fontWeight: "600",
    border: "solid 1px #BDBDBD",
};
const cellStyle = {
    border: "solid 1px #BDBDBD",
};

export const CustomPropertiesTable = ({ customProperties, setCustomProperties }: Props) => {
    const [dialogOpen, setDialogOpen] = useState(false);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const { setAlert } = useContext(AlertContext);
    const [deletePropertyInfo, setDeletePropertyInfo] = useState({ name: "", id: "" });
    const [editPropertyId, setEditPropertyId] = useState("");
    const { t } = useTranslation();

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

    const columns: GridColDef[] = [
        {
            field: "name",
            headerName: t("admin.settings.tableHeader.name"),
            display: "flex",
            width: 400,
            renderCell: (params) => (params.row.type === "FIXED" ? t(`admin.settings.fixedPropertyType.${params.row.name}`) : params.row.name),
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            headerClassName: "header-style",
            cellClassName: "cell-style",
            filterable: true,
            sortable: true,
        },
        {
            field: "propertyType",
            headerName: t("admin.settings.tableHeader.propertyType"),
            display: "flex",
            width: 200,
            renderCell: (params) => t(`admin.settings.propertyType.${params.row.propertyType}`),
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            headerClassName: "header-style",
            cellClassName: "cell-style",
            filterable: true,
            sortable: true,
        },
        {
            field: "objectType",
            headerName: t("admin.settings.tableHeader.objectType"),
            display: "flex",
            width: 200,
            renderCell: (params) => t(`admin.settings.objectType.${params.row.objectType}`),
            headerClassName: "header-style",
            cellClassName: "cell-style",
            filterable: false,
            sortable: false,
        },
        {
            field: "categories",
            headerName: t("admin.settings.tableHeader.categories"),
            display: "flex",
            width: 700,
            renderCell: (params) => (
                <Box>
                    {params.row.categories?.map(
                        (category: CategoryType, index: number) =>
                            !category.disabled && <Chip key={category.id ?? "" + index} variant="outlined" label={category.name} />,
                    )}
                    {params.row.ordinals
                        ?.sort((a: OrdinalCategoryType, b: OrdinalCategoryType) => a.level - b.level)
                        .map(
                            (ordinalCategory: OrdinalCategoryType, index: number) =>
                                !ordinalCategory.disabled && <Chip key={ordinalCategory.id ?? "" + index} variant="outlined" label={ordinalCategory.name} />,
                        )}
                </Box>
            ),
            headerClassName: "header-style",
            cellClassName: "cell-style",
            filterable: false,
            sortable: false,
        },
        {
            field: "actions",
            type: "actions",
            headerName: t("admin.settings.tableHeader.actions"),
            getActions: (params) => {
                const actions = [
                    <Tooltip title={t("admin.settings.tableHeader.edit")} key="edit">
                        <GridActionsCellItem
                            size="large"
                            icon={<EditIcon />}
                            label={t("admin.settings.tableHeader.edit")}
                            disabled={params.row.disabled}
                            onClick={() => {
                                setEditPropertyId(params.row.id);
                                setEditDialogOpen(true);
                            }}
                            sx={{
                                bgcolor: "#31456F",
                                color: "white",
                                "&:hover": {
                                    bgcolor: "navy",
                                },
                            }}
                        />
                    </Tooltip>,
                ];
                if (params.row.type === "CUSTOM") {
                    actions.unshift(
                        <Tooltip title={t("admin.settings.tableHeader.delete")} key="delete">
                            <GridActionsCellItem
                                size="large"
                                icon={<DeleteIcon />}
                                label={t("admin.settings.tableHeader.delete")}
                                disabled={params.row.disabled}
                                onClick={() => handleDelete(params.row.id, params.row.name)}
                                sx={{
                                    bgcolor: "tomato",
                                    color: "white",
                                    "&:hover": {
                                        bgcolor: "red",
                                    },
                                }}
                            />
                        </Tooltip>,
                    );
                }
                return actions;
            },
            display: "flex",
            width: 194,
            headerClassName: "header-style",
            cellClassName: "cell-style",
            headerAlign: "right",
            align: "right",
            filterable: false,
            sortable: false,
        },
    ];

    return (
        <>
            <Stack
                width="100%"
                sx={{
                    margin: "0 auto",
                    overflowX: "auto",
                }}
            >
                <DataGrid
                    rows={customProperties.filter((row) => !row.disabled)}
                    rowHeight={70}
                    rowSelection={false}
                    columns={columns}
                    initialState={{
                        pagination: {
                            paginationModel: { page: 0, pageSize: 10 },
                        },
                    }}
                    pageSizeOptions={[5, 10, 25, 50, 100]}
                    getRowId={(row) => row.id}
                    sx={{
                        "& .header-style": headerStyle,
                        "& .cell-style": cellStyle,
                    }}
                    autoHeight
                />
            </Stack>
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
            <PropertyDialog setCustomProperties={setCustomProperties} openDialog={editDialogOpen} setOpenDialog={setEditDialogOpen} id={editPropertyId} />
        </>
    );
};
