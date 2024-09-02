import { Box, Stack, Typography } from "@mui/material";
import { DataGrid, GridCellParams } from "@mui/x-data-grid";
import { t } from "i18next";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import PriceCategoriesDialog from "./PriceCategoriesDialog";
import { useContext, useEffect, useState } from "react";
import { CategoryType, Property, updateCustomProperty } from "../../api/adminSettingServices";
import DeleteDialog from "./DeleteDialog";
import AlertContext from "../../context/AlertContext";
import useAllowedActions from "../../hooks/useAllowedActions";
import { formatMonetaryValue } from "../../utils/inputHelpers";

type Category = {
    id: string;
    name: string;
    min: number | null;
    max?: number;
    disabled: boolean;
};

type Props = {
    property: Property;
    setRangeCategories: (rangeCategories: Property[]) => void;
};

const PriceCategoriesTable = ({ property, setRangeCategories }: Props) => {
    const [categoryToDelete, setCategoryToDelete] = useState<Category | null>(null);
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [categoryToEdit, setCategoryToEdit] = useState<Category | null>(null);
    const [rows, setRows] = useState<Category[]>([]);
    const { setAlert } = useContext(AlertContext);
    const [categories, setCategories] = useState<CategoryType[]>([]);

    const { allowedActions } = useAllowedActions();

    useEffect(() => {
        const rows = (property.ranges ?? []).filter((range) => !range.disabled);
        setRows(rows as Category[]);

        const categories = rows.map((range) => {
            return {
                id: range.id,
                name: range.name,
                disabled: range.disabled,
            };
        });
        setCategories(categories);
    }, [property.ranges]);

    const handleDelete = async () => {
        if (!categoryToDelete || !property.id || !property.ranges || !allowedActions.includes("EDIT_CUSTOM_PROPERTIES")) return;
        const updatedRanges = property.ranges.map((range) => (range.id === categoryToDelete.id ? { ...range, disabled: true } : range));

        const updatedProperty = {
            ...property,
            ranges: updatedRanges,
        };

        try {
            const savedProperty = await updateCustomProperty(property.id, updatedProperty);
            setRangeCategories([savedProperty]);
            setAlert(t("admin.priceCategories.successfullyDeleted"), "success");
        } catch (error) {
            if (error instanceof Error) setAlert(error.message, "error");
        } finally {
            closeDeleteDialog();
        }
    };

    const closeDeleteDialog = () => {
        setOpenDeleteDialog(false);
        setCategoryToDelete(null);
    };
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
                    <Typography>€{formatMonetaryValue(params.row.min)}</Typography>
                    <Typography>{params.row.max ? `- €${formatMonetaryValue(params.row.max)}` : t("generic.andMore")}</Typography>
                </Box>
            ),
        },
        {
            field: "acties",
            headerName: t("admin.priceCategories.actions"),
            flex: 0.5,
            sortable: true,
            renderCell: (params: GridCellParams) =>
                allowedActions.includes("EDIT_CUSTOM_PROPERTIES") ? (
                    <Box display="flex" alignItems="center" justifyContent="center" style={{ height: "100%" }} gap="10px">
                        <EditOutlinedIcon
                            style={{ cursor: "pointer" }}
                            color="primary"
                            onClick={() => {
                                setCategoryToEdit(params.row);
                                setDialogOpen(true);
                            }}
                        />
                        <DeleteForeverOutlinedIcon
                            style={{ cursor: "pointer" }}
                            color="error"
                            onClick={() => {
                                setCategoryToDelete(params.row);
                                setOpenDeleteDialog(true);
                            }}
                        />
                    </Box>
                ) : null,
        },
    ];
    return (
        <>
            <DataGrid
                autoHeight
                rows={rows || []}
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
                mt={3}
                mb={7}
                sx={{ cursor: "pointer" }}
                onClick={() => {
                    setDialogOpen(true);
                }}
            >
                {allowedActions.includes("EDIT_CUSTOM_PROPERTIES") && (
                    <>
                        <AddCircleIcon color="primary" sx={{ fontSize: "40px" }} />
                        {t("admin.priceCategories.add")}
                    </>
                )}
            </Stack>

            <PriceCategoriesDialog
                open={dialogOpen}
                setOpen={setDialogOpen}
                setRangeCategories={setRangeCategories}
                id={property.id}
                propertyName={property.name}
                categoryToEdit={categoryToEdit}
                setCategoryToEdit={setCategoryToEdit}
                title={property.name === "priceRangeBuy" ? t("admin.priceCategories.priceCategoryBuy") : t("admin.priceCategories.priceCategoryRent")}
                categories={categories}
            />

            <DeleteDialog open={openDeleteDialog} handleDelete={handleDelete} closeDeleteDialog={closeDeleteDialog} />
        </>
    );
};

export default PriceCategoriesTable;
