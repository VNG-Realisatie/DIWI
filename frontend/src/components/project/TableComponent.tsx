import {
    GridColDef,
    GridFilterModel,
    GridPaginationModel,
    GridRowParams,
    GridRowSelectionModel,
    GridSortModel,
    GridColumnVisibilityModel,
    GridLocaleText,
    DataGrid,
    GridColumnResizeParams,
    GridSlotsComponent,
} from "@mui/x-data-grid";

type Props = {
    showCheckBox: boolean;
    // @ts-ignore
    rows: any[];
    columns: GridColDef[];
    setPaginationInfo?: (model: GridPaginationModel) => void;
    rowCount?: number;
    paginationMode?: "client" | "server";
    onRowClick?: (params: GridRowParams) => void;
    filterModel?: GridFilterModel;
    handleFilterModelChange?: (model: GridFilterModel) => void;
    sortModel?: GridSortModel;
    handleSortModelChange?: (model: GridSortModel) => void;
    selectionModel?: GridRowSelectionModel;
    columnVisibilityModel?: GridColumnVisibilityModel;
    onColumnVisibilityModelChange?: (model: GridColumnVisibilityModel) => void;
    onRowSelectionModelChange?: (model: GridRowSelectionModel) => void;
    onColumnResize?: (params: GridColumnResizeParams) => void;
    localeText?: GridLocaleText;
    isRowSelectable?: (params: GridRowParams) => boolean;
    slots?: Partial<GridSlotsComponent>;
};

const TableComponent = ({
    showCheckBox,
    rows,
    columns,
    setPaginationInfo,
    rowCount,
    paginationMode = "client",
    onRowClick,
    filterModel,
    handleFilterModelChange,
    sortModel,
    handleSortModelChange,
    selectionModel,
    columnVisibilityModel,
    onColumnVisibilityModelChange,
    onRowSelectionModelChange,
    onColumnResize,
    localeText,
    isRowSelectable,
    slots,
}: Props) => {
    return (
        <DataGrid
            autoHeight
            sx={{
                borderRadius: 0,
            }}
            checkboxSelection={showCheckBox}
            rows={rows}
            columns={columns}
            rowHeight={70}
            initialState={{
                pagination: {
                    paginationModel: { page: 0, pageSize: 10 },
                },
            }}
            pageSizeOptions={[5, 10, 25, 50, 100]}
            onPaginationModelChange={(model) => {
                setPaginationInfo && setPaginationInfo({ page: model.page + 1, pageSize: model.pageSize });
            }}
            rowCount={rowCount}
            paginationMode={paginationMode}
            onRowClick={onRowClick}
            filterModel={filterModel}
            onFilterModelChange={handleFilterModelChange}
            sortModel={sortModel}
            onSortModelChange={handleSortModelChange}
            keepNonExistentRowsSelected
            rowSelectionModel={selectionModel}
            columnVisibilityModel={columnVisibilityModel}
            onColumnVisibilityModelChange={onColumnVisibilityModelChange}
            onRowSelectionModelChange={onRowSelectionModelChange}
            onColumnResize={onColumnResize}
            localeText={localeText}
            isRowSelectable={isRowSelectable}
            slots={slots}
        />
    );
};

export default TableComponent;
