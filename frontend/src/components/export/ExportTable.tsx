import { Box } from "@mui/material";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import { deleteExportData, ExportData } from "../../api/exportServices";
import { useNavigate } from "react-router-dom";
import { useContext, useState } from "react";
import DeleteDialogWithConfirmation from "../admin/user-management/DeleteDialogWithConfirmation";
import AlertContext from "../../context/AlertContext";
import { t } from "i18next";

type Props = {
    exportData: ExportData[];
    selectedExport: ExportData | null;
    setSelectedExport: (data: ExportData | null) => void;
    setExportData: (data: ExportData[]) => void;
};

const ExportTable = ({ exportData, selectedExport, setSelectedExport, setExportData }: Props) => {
    const navigate = useNavigate();
    const [idToDelete, setIdToDelete] = useState<string>("");
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const { setAlert } = useContext(AlertContext);

    const handleDelete = async (id: string) => {
        try {
            await deleteExportData(id);
            setExportData(exportData.filter((data) => data.id !== idToDelete));
            setAlert(t("admin.export.notification.deleted"), "success");
            setDeleteDialogOpen(false);
            setIdToDelete("");
        } catch (error: unknown) {
            if (error instanceof Error) setAlert(error.message, "error");
        }
    };

    const columns: GridColDef[] = [
        { field: "name", headerName: t("admin.export.name"), flex: 1 },
        { field: "type", headerName: t("admin.export.type"), flex: 1 },
        {
            field: "actions",
            headerName: "Actions",
            flex: 1,
            sortable: false,
            renderCell: (params) => (
                <Box display="flex" alignItems="center" justifyContent="center" style={{ height: "100%" }} gap="10px">
                    <EditOutlinedIcon style={{ cursor: "pointer" }} color="primary" onClick={() => navigate(`/admin/export-settings/${params.row.id}`)} />
                    <DeleteForeverOutlinedIcon
                        style={{ cursor: "pointer" }}
                        color="error"
                        onClick={() => {
                            setIdToDelete(params.row.id);
                            setDeleteDialogOpen(true);
                        }}
                    />
                </Box>
            ),
        },
    ];

    return (
        <Box p={2}>
            <DataGrid
                rows={exportData}
                columns={columns}
                initialState={{
                    pagination: {
                        paginationModel: { page: 0, pageSize: 10 },
                    },
                }}
                pageSizeOptions={[5, 10, 25]}
                onRowClick={(params) => setSelectedExport(params.row)}
                getRowClassName={(params) => (selectedExport?.id === params.row.id ? "selected-row" : "")}
                autoHeight
                checkboxSelection
                rowSelectionModel={selectedExport ? [selectedExport.id] : []}
                onRowSelectionModelChange={(newSelection) => {
                    const selectedId = newSelection.length > 0 ? newSelection[newSelection.length - 1] : null;
                    const selectedData = exportData.find((data) => data.id === selectedId) || null;
                    setSelectedExport(selectedData);
                }}
            />
            <DeleteDialogWithConfirmation
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
                onConfirm={() => handleDelete(idToDelete)}
                dialogContentText="admin.export.deleteConfirmation"
            />
        </Box>
    );
};

export default ExportTable;
