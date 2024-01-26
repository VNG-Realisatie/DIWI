import { FormControl, FormControlLabel, Popover, Stack, Switch, Typography } from "@mui/material";
import { ReactComponent as Map } from "../assets/temp/map.svg";
import { useState } from "react";
import { ProjectsTableView } from "../components/ProjectsTableView";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import { DateCalendar } from "@mui/x-date-pickers";
import dayjs from "dayjs";

export const Projects = () => {
    const [tableview, setTableView] = useState(false);
    const handleTableSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTableView(e.target.checked);
    };
    const navigate = useNavigate();
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);
    const handleClose = () => {
        setAnchorEl(null);
    };
    const [selectedDate, setSelectedDate] = useState<any>();
    const open = Boolean(anchorEl);
    const id = open ? "simple-popover" : undefined;

    const convertedDate = selectedDate && new Date(selectedDate).toISOString().split("T")[0];
    return (
        <Stack direction="row" justifyContent="space-between">
            {/* TODO ADD BREADCRUMB COMPONENT LATER */}
            <Stack direction="column">
                <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ backgroundColor: "#002C64", color: "#FFFFFF" }} p={1}>
                    <Typography> Projecten overzicht: </Typography>
                    <Typography
                        onClick={(event: React.MouseEvent<HTMLButtonElement>) => {
                            setAnchorEl(event.currentTarget);
                        }}
                    >
                        Peildatum: {convertedDate ? convertedDate : "2023-09-05"}
                    </Typography>
                    <Popover
                        id={id}
                        open={open}
                        anchorEl={anchorEl}
                        onClose={handleClose}
                        anchorOrigin={{
                            vertical: "bottom",
                            horizontal: "left",
                        }}
                    >
                        <DateCalendar defaultValue={dayjs("2023-09-05")} onChange={(newValue) => setSelectedDate(newValue)} />
                    </Popover>
                </Stack>
                <Stack direction="row" justifyContent="flex-end" alignItems="center" border="solid 1px #ddd" p={0.5}>
                    <FormControl component="fieldset" variant="standard">
                        <FormControlLabel control={<Switch checked={tableview} onChange={handleTableSelect} name="table" />} label="Tabel weergave " />
                    </FormControl>
                    <AddCircleIcon color="info" sx={{ fontSize: "45px", cursor: "pointer" }} onClick={() => navigate(Paths.projectAdd.path)} />
                </Stack>
                {!tableview && <Map style={{ width: "100%" }} />}
                {tableview && <ProjectsTableView />}
            </Stack>
        </Stack>
    );
};
