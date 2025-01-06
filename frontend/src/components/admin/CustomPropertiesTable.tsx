import { Box, Chip, Dialog, DialogActions, DialogTitle, Button, Tooltip, Stack } from "@mui/material";
import { DataGrid, GridColDef, GridActionsCellItem, getGridStringOperators } from "@mui/x-data-grid";
import { useTranslation } from "react-i18next";
import { CategoryType, OrdinalCategoryType, CustomPropertyStoreType, Property } from "../../api/adminSettingServices";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { useContext, useState } from "react";
import AlertContext from "../../context/AlertContext";
import PropertyDialog from "./PropertyDialog";
import ActionNotAllowed from "../../pages/ActionNotAllowed";
import UserContext from "../../context/UserContext";
import { useCustomPropertyStore } from "../../context/CustomPropertiesContext";
import { observer } from "mobx-react-lite";

export const CustomPropertiesTable = observer(() => {
    const [dialogOpen, setDialogOpen] = useState(false);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const { setAlert } = useContext(AlertContext);
    const [deletePropertyInfo, setDeletePropertyInfo] = useState({ name: "", id: "" });
    const [editPropertyId, setEditPropertyId] = useState("");
    const { t } = useTranslation();
    const { allowedActions } = useContext(UserContext);
    const { customProperties, deleteCustomProperty }: CustomPropertyStoreType = useCustomPropertyStore();

    if (!allowedActions.includes("VIEW_CUSTOM_PROPERTIES")) {
        return <ActionNotAllowed errorMessage={t("customProperties.forbidden")} />;
    }

    const handleDelete = async (id: string, name: string) => {
        setDialogOpen(true);
        setDeletePropertyInfo({ id, name });
    };

    const handleDialogDelete = async () => {
        try {
            deleteCustomProperty(deletePropertyInfo.id);
            setAlert(t("admin.settings.notifications.successfullyDeleted"), "success");
        } catch (error) {
            console.error("Failed to delete custom property:", error);
            // Optionally, you can set an error alert here
        } finally {
            setDialogOpen(false);
        }
    };

    const sortCustomProperties = (properties: Property[]) => {
        return properties.sort((a, b) => {
            const nameA = a.type === "FIXED" ? t(`admin.settings.fixedPropertyType.${a.name}`) : a.name;
            const nameB = b.type === "FIXED" ? t(`admin.settings.fixedPropertyType.${b.name}`) : b.name;
            return nameA.localeCompare(nameB);
        });
    };
    const columns: GridColDef[] = [
        {
            field: "name",
            headerName: t("admin.settings.tableHeader.name"),
            flex: 0.5,
            renderCell: (params) => (params.row.type === "FIXED" ? t(`admin.settings.fixedPropertyType.${params.row.name}`) : params.row.name),
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            filterable: true,
            sortable: true,
            sortComparator: (v1, v2, cellParams1, cellParams2) => {
                const row1 = cellParams1.api.getRow(cellParams1.id);
                const row2 = cellParams2.api.getRow(cellParams2.id);
                const name1 = row1.type === "FIXED" ? t(`admin.settings.fixedPropertyType.${row1.name}`) : row1.name;
                const name2 = row2.type === "FIXED" ? t(`admin.settings.fixedPropertyType.${row2.name}`) : row2.name;
                return name1.localeCompare(name2);
            },
        },
        {
            field: "propertyType",
            headerName: t("admin.settings.tableHeader.propertyType"),
            flex: 0.25,
            renderCell: (params) => t(`admin.settings.propertyType.${params.row.propertyType}`),
            filterOperators: getGridStringOperators().filter((o) => o.value === "contains"),
            filterable: true,
            sortable: true,
        },
        {
            field: "objectType",
            headerName: t("admin.settings.tableHeader.objectType"),
            flex: 0.25,
            renderCell: (params) => t(`admin.settings.objectType.${params.row.objectType}`),
            filterable: false,
            sortable: false,
        },
        {
            field: "categories",
            headerName: t("admin.settings.tableHeader.categories"),
            flex: 1,
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
            filterable: false,
            sortable: false,
        },
        {
            field: "actions",
            type: "actions",
            headerName: t("admin.settings.tableHeader.actions"),
            getActions: (params) => {
                const actions = allowedActions.includes("EDIT_CUSTOM_PROPERTIES")
                    ? [
                          <Tooltip title={t("admin.settings.tableHeader.edit")} key="edit">
                              <GridActionsCellItem
                                  id={params.row.name}
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
                      ]
                    : [];
                if (params.row.type === "CUSTOM" && allowedActions.includes("EDIT_CUSTOM_PROPERTIES")) {
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
            flex: 0.25,
            headerAlign: "right",
            align: "right",
            filterable: false,
            sortable: false,
        },
    ];

    return (
        <>
            <Stack id="custom-properties-table">
                <DataGrid
                    rows={sortCustomProperties(customProperties.filter((row) => !row.disabled && row.propertyType !== "RANGE_CATEGORY"))}
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
            <PropertyDialog openDialog={editDialogOpen} setOpenDialog={setEditDialogOpen} id={editPropertyId} setId={setEditPropertyId} />
        </>
    );
});
