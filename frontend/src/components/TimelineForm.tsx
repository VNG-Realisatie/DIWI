import {
  Autocomplete,
  Box,
  Dialog,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import {
  DateCalendar,
  DatePicker,
  LocalizationProvider,
} from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import {
  HouseBlockItem,
  StatusType,
  houseBlocks,
  statuses,
} from "../api/dummyData";
import { useState } from "react";
export const TimelineForm = (props: any) => {
  const [showDialog, setShowDialog] = useState(false);
  const handleClose = () => {
    setShowDialog(false);
  };
  const handleDialogOpen = () => {
    setShowDialog(true);
  };
  return (
    <Box mt={4} position="relative">
      <Typography variant="h6" fontWeight="600">
        Vul de gegevens voor de tijdlijn in
      </Typography>

      <Typography variant="subtitle1" fontWeight="500">
        Opleverdatum*
      </Typography>
        <DatePicker
          value={
            props.createProjectForm ? props.createProjectForm.opleverDatum : ""
          }
          onChange={(newValue) =>
            props.setCreateProjectForm({
              ...props.createProjectForm,
              opleverDatum: newValue,
            })
          }
        />
      <Stack
        direction="row"
        alignItems="flex-end"
        justifyContent="space-between"
        position="relative"
      >
        <Autocomplete
          sx={{ width: "250px" }}
          options={houseBlocks}
          getOptionLabel={(option: HouseBlockItem) => option.name}
          value={
            props.createProjectForm ? props.createProjectForm.houseBlock : ""
          }
          onChange={(event: any, newValue: HouseBlockItem | null) => {
            props.setCreateProjectForm({
              ...props.createProjectForm,
              houseBlock: newValue,
            });
          }}
          renderInput={(params) => (
            <TextField {...params} label="Huizenblok" variant="standard" />
          )}
        />
        <Typography variant="body1">Verwachte oplevering</Typography>
        <Autocomplete
          sx={{ width: "250px" }}
          options={statuses}
          getOptionLabel={(option: StatusType) => option.status}
          value={props.createProjectForm ? props.createProjectForm.status : ""}
          onChange={(event: any, newValue: StatusType | null) => {
            props.setCreateProjectForm({
              ...props.createProjectForm,
              status: newValue,
            });
          }}
          renderInput={(params) => (
            <TextField {...params} label="Toestand" variant="standard" />
          )}
        />
        <Typography variant="body1">is</Typography>

        <DatePicker

          value={
            props.createProjectForm ? props.createProjectForm.statusDate : ""
          }
          onChange={(newValue) =>
            props.setCreateProjectForm({
              ...props.createProjectForm,
              statusDate: newValue,
            })
          }
        />
      </Stack>
    </Box>
  );
};
