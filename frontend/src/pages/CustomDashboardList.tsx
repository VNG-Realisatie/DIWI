import React, { useState, useEffect } from "react";
import { DataGrid, GridCellParams, GridColDef } from "@mui/x-data-grid";

import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Button, Box } from "@mui/material";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import { Blueprint, deleteBlueprint, getBlueprints } from "../api/dashboardServices";
import UserGroupSelect from "../widgets/UserGroupSelect";
import { t } from "i18next";
import useAlert from "../hooks/useAlert";

export const CustomDashboardList = () => {
    const [blueprints, setBlueprints] = useState<Blueprint[]>([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedBlueprint, setSelectedBlueprint] = useState<Blueprint | null>(null);
    const { setAlert } = useAlert();

    useEffect(() => {
        const fetchBlueprints = async () => {
            const fetchedBlueprints = await getBlueprints();
            setBlueprints(fetchedBlueprints);
        };

        fetchBlueprints();
    }, []);

    const handleDeleteClick = (blueprint: Blueprint) => {
        setSelectedBlueprint(blueprint);
        setOpenDialog(true);
    };

    const handleConfirmDelete = async () => {
        if (selectedBlueprint) {
            try {
                await deleteBlueprint(selectedBlueprint);
                setBlueprints(blueprints.filter((bp) => bp.uuid !== selectedBlueprint.uuid));
                setOpenDialog(false);
                setSelectedBlueprint(null);
                setAlert(t("dashboard.blueprints.successfullyDeleted"), "success");
            } catch (error: unknown) {
                if (error instanceof Error) {
                    setAlert(error.message, "error");
                }
            }
        }
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
        setSelectedBlueprint(null);
    };

    const columns: GridColDef[] = [
        { field: "name", headerName: t("dashboard.blueprints.table.name"), flex: 3 },
        {
            field: "userGroups",
            headerName: t("dashboard.blueprints.table.users"),
            flex: 5,
            sortable: false,
            renderCell: (cellValues) => {
                return (
                    cellValues.row.userGroups &&
                    cellValues.row.userGroups.length > 0 && (
                        <UserGroupSelect mandatory={false} errorText="" readOnly={true} userGroup={cellValues.row.userGroups} setUserGroup={() => {}} />
                    )
                );
            },
        },
        {
            field: "actions",
            headerName: t("admin.userManagement.tableHeader.actions"),
            sortable: false,
            flex: 1,
            renderCell: (params: GridCellParams) => (
                <Box display="flex" alignItems="center" justifyContent="center" style={{ height: "100%" }} gap="10px">
                    <EditOutlinedIcon style={{ cursor: "pointer" }} color="primary" onClick={() => {}} />
                    <DeleteForeverOutlinedIcon style={{ cursor: "pointer" }} color="error" onClick={() => handleDeleteClick(params.row)} />
                </Box>
            ),
        },
    ];

    return (
        <>
            <DataGrid
                autoHeight
                rows={blueprints}
                columns={columns}
                getRowId={(row) => row.uuid}
                initialState={{
                    pagination: {
                        paginationModel: { page: 0, pageSize: 10 },
                    },
                }}
                pageSizeOptions={[5, 10, 25]}
                disableRowSelectionOnClick
            />
            <Dialog open={openDialog} onClose={handleCloseDialog}>
                <DialogTitle>{t("dashboard.blueprints.dialogTitle")}</DialogTitle>
                <DialogContent>
                    <DialogContentText>{t("dashboard.blueprints.deleteConfirmation")}</DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog} variant="outlined">
                        {t("generic.no")}
                    </Button>
                    <Button onClick={handleConfirmDelete} variant="contained" autoFocus>
                        {t("generic.yes")}
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
};
