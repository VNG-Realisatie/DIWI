import {
    FormControl,
    FormControlLabel,
    Stack,
    Switch,
} from "@mui/material";
import { ReactComponent as Map } from "../assets/temp/map.svg";
import { useState } from "react";
import { ProjectsTableView } from "../components/ProjectsTableView";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import BreadcrumbBar from "../components/header/BreadcrumbBar";

export const Projects = () => {
    const [tableview, setTableView] = useState(false);
    const handleTableSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTableView(e.target.checked);
    };
    const navigate = useNavigate();

    return (
        <Stack
            direction="column"
            justifyContent="space-between"
            position="relative"
            border="solid 1px #ddd"
            mb={10}
        >
                <BreadcrumbBar breadcrumb={["Projecten"]} />

                <Stack direction="row" justifyContent="flex-end" alignItems="center" border="solid 1px #ddd" p={0.5}>
                    <FormControl component="fieldset" variant="standard">
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={tableview}
                                    onChange={handleTableSelect}
                                    name="table"
                                />
                            }
                            label="Tabel weergave "
                        />
                    </FormControl>
                    <AddCircleIcon
                        color="info"
                        sx={{ fontSize: "45px", cursor: "pointer" }}
                        onClick={() => navigate(Paths.projectAdd.path)}
                    />
                </Stack>
                {!tableview && <Map style={{ width: "100%" }} />}
                {tableview && <ProjectsTableView />}
        </Stack>
    );
};
