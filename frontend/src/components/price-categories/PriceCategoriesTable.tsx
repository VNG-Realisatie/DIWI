import { Box, Stack, Typography } from "@mui/material";
import { DataGrid, GridCellParams } from "@mui/x-data-grid";
import { t } from "i18next";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import PriceCategoriesDialog from "./PriceCategoriesDialog";
import { useState } from "react";
import { Property } from "../../api/adminSettingServices";

type Row = {
    id?: string;
    name: string;
    min: number;
    max?: number;
    disabled: boolean;
};

type Props = {
    row: Row[];
    property: Property;
    setRangeCategories: (rangeCategories: Property[]) => void;
};

const PriceCategoriesTable = ({ property, setRangeCategories }: Props) => {
    if (!property) {
        return;
    }
    const [dialogOpen, setDialogOpen] = useState(false);
    const columns = [
        {
            field: "name",
            headerName: t("admin.priceCategories.name"),
            flex: 2.5,
            sortable: true,
        },
        {
            field: "amount",
            headerName: t("admin.priceCategories.amount"),
            flex: 4,
            renderCell: (params: GridCellParams) => (
                <Box display="flex" alignItems="center" justifyContent="center" style={{ height: "100%" }} gap="10px">
                    <Typography>{params.row.min}</Typography>
                    {params.row.max && <Typography>- {params.row.max}</Typography>}
                </Box>
            ),
        },
        {
            field: "acties",
            headerName: t("admin.userManagement.tableHeader.actions"),
            flex: 0.5,
            sortable: true,
            renderCell: (params: GridCellParams) => (
                <Box display="flex" alignItems="center" justifyContent="center" style={{ height: "100%" }} gap="10px">
                    <EditOutlinedIcon style={{ cursor: "pointer" }} color="primary" onClick={() => {}} />
                    <DeleteForeverOutlinedIcon style={{ cursor: "pointer" }} color="error" onClick={() => {}} />
                </Box>
            ),
        },
    ];
    return (
        <>
            <Typography variant="h6" gutterBottom component="div">
                {t("admin.userManagement.titleUser")}
            </Typography>
            <DataGrid
                autoHeight
                rows={property.ranges || []}
                columns={columns}
                initialState={{
                    pagination: {
                        paginationModel: { page: 0, pageSize: 10 },
                    },
                }}
                pageSizeOptions={[5, 10, 25]}
                disableRowSelectionOnClick
            />
            <Stack
                direction="row"
                alignItems="center"
                mt={1}
                sx={{ cursor: "pointer" }}
                onClick={() => {
                    setDialogOpen(true);
                }}
            >
                <AddCircleIcon color="primary" sx={{ fontSize: "40px" }} />
                {t("admin.userManagement.addUser")}
            </Stack>

            <PriceCategoriesDialog
                open={dialogOpen}
                setOpen={setDialogOpen}
                setRangeCategories={setRangeCategories}
                id={property.id}
                propertyName={property.name}
            />
        </>
    );
};

export default PriceCategoriesTable;
