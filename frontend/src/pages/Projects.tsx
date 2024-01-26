import {
    Stack
} from "@mui/material";
import { ReactComponent as Map } from "../assets/temp/map.svg";
import { ProjectsTableView } from "../components/ProjectsTableView";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useLocation, useNavigate } from "react-router-dom";
import * as Paths from "../Paths";
import BreadcrumbBar from "../components/header/BreadcrumbBar";

export const Projects = () => {
    const navigate = useNavigate();
    const location = useLocation();

    return (
        <Stack
            direction="column"
            justifyContent="space-between"
            position="relative"
            border="solid 1px #ddd"
            mb={10}
        >
                <BreadcrumbBar pageTitle="Projecten overzicht" links={[{title: "Kaart", link: Paths.projects.path}, {title: "Tabel", link: Paths.projectsTable.path}]} />

                <Stack direction="row" justifyContent="flex-end" alignItems="center" border="solid 1px #ddd" p={0.5}>

                    <AddCircleIcon
                        color="info"
                        sx={{ fontSize: "45px", cursor: "pointer" }}
                        onClick={() => navigate(Paths.projectAdd.path)}
                    />
                </Stack>
                { ( location.pathname === Paths.projects.path ||
                    location.pathname === Paths.root.path
                    ) && <Map style={{ width: "100%" }} /> }
                { location.pathname === Paths.projectsTable.path && <ProjectsTableView /> }
        </Stack>
    );
};
