import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { GridRowParams } from "@mui/x-data-grid";
import { projects } from "../api/dummyData";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogTitle,
  Stack,
} from "@mui/material";
import useAlert from "../hooks/useAlert";

//Todo Implement filterDataWithSelectedColumns here to get columns dynamic.
const columns: GridColDef[] = [
  { field: "id", headerName: "ID", width: 90 },
  {
    field: "name",
    headerName: "Name",
    width: 200,
    editable: false,
  },
  {
    field: "geo",
    headerName: "Geography",
    width: 200,
    editable: false,
  },
  {
    field: "organization",
    headerName: "Organisatie",
    width: 200,
    editable: false,
  },
  {
    field: "color",
    headerName: "Color",
    width: 200,
    editable: false,
  },
];

const rows = projects;
interface RowData {
  id: number;
}
type Props = {
  showCheckBox?: boolean;
};
export const ProjectsTableView = ({ showCheckBox }: Props) => {
  const navigate = useNavigate();
  const [selectedRows, setSelectedRows] = useState<any[]>([]);
  const [showDialog, setShowDialog] = useState(false);
  const { setAlert } = useAlert();
  const handleRowClick = (params: GridRowParams) => {
    const clickedRow: RowData = params.row as RowData;
    navigate(`/projects/${clickedRow.id}`);
  };
  const handleExport = (params: any) => {
    const clickedRow: RowData = params.row as RowData;
    if (selectedRows.includes(clickedRow.id)) {
      setSelectedRows(selectedRows.filter((s) => s !== clickedRow.id));
    } else {
      setSelectedRows([...selectedRows, clickedRow.id]);
    }
  };
  const handleClose = () => setShowDialog(false);
  console.log(selectedRows);
  return (
    <Stack width="100%">
      <DataGrid
        checkboxSelection={showCheckBox}
        rows={rows}
        columns={columns}
        initialState={{
          pagination: {
            paginationModel: {
              pageSize: 10,
            },
          },
        }}
        pageSizeOptions={[5]}
        onRowClick={showCheckBox ? handleExport : handleRowClick}
      />
      <Dialog
        open={showDialog}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">Weet je het zeker?</DialogTitle>

        <DialogActions sx={{ px: 5, py: 3, ml: 15 }}>
          <Button onClick={handleClose}>Annuleer</Button>
          <Button
            variant="contained"
            onClick={() => {
              handleClose();
              setAlert(
                "Gelukt! Je ontvangt de bevestiging via de mail.",
                "success"
              );
            }}
            autoFocus
          >
            Exporteer
          </Button>
        </DialogActions>
      </Dialog>
      {showCheckBox && (
        <Button
          sx={{ width: "130px", my: 2, ml: "auto" }}
          variant="contained"
          onClick={() => {
            setShowDialog(true);
          }}
        >
          Exporteren
        </Button>
      )}
    </Stack>
  );
};
