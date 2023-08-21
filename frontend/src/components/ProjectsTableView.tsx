import * as React from "react";
import Box from "@mui/material/Box";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import {  GridRowParams } from '@mui/x-data-grid';
import { projects } from "../api/dummyData";
import { useNavigate } from "react-router-dom";

const columns: GridColDef[] = [
  { field: "id", headerName: "ID", width: 90 },
  {
    field: "name",
    headerName: "Name",
    width: 300,
    editable: false,
  },
  {
    field: "geo",
    headerName: "Geography",
    width: 300,
    editable: false,
  },
  {
    field: "color",
    headerName: "Color",
    width: 230,
    editable: false,
  },
];

const rows = projects;
interface RowData {
    id: number;
  }

export const ProjectsTableView = () => {
    const navigate = useNavigate();
    const handleRowClick = (params: GridRowParams) => {
        const clickedRow: RowData = params.row as RowData;
        navigate(`/projects/${clickedRow.id}`); 
      };
    
  return (
    <Box width="100%">
      <DataGrid
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
        onRowClick={handleRowClick}
      />
    </Box>
  );
};
