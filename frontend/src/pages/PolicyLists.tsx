import { Button, Dialog, DialogContent, DialogTitle, Stack, TextField } from "@mui/material";
import { policyGoals } from "../api/dummyData";
import { PolicyCard } from "../components/PolicyCard";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import { useState } from "react";
import BreadcrumbBar from "../components/header/BreadcrumbBar";
import { useLocation } from "react-router-dom";
import * as Paths from "../Paths";

interface DataPolicy {
    id: number;
    name: string;
    data: {
        characteristic: string;
        goal: number | string;
        time: string;
        geo: string;
        category: string;
    };
}

//Todo Decide if i want to get policy context or as prop
export const PolicyLists = () => {
    const [showDialog, setShowDialog] = useState(false);
    const [dataPolicy, setDataPolicy] = useState<DataPolicy>({
        id: 0,
        name: "",
        data: {
            characteristic: "",
            goal: "",
            time: "",
            geo: "",
            category: "",
        },
    });
    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = event.target;
        if (name === "name") {
            setDataPolicy({ ...dataPolicy, name: value });
        } else {
            setDataPolicy((prevDataPolicy) => ({
                ...prevDataPolicy,
                data: {
                    ...prevDataPolicy.data,
                    [name]: value,
                },
            }));
        }
    };
    const location = useLocation();
    return (
        <Stack>
            <BreadcrumbBar
                pageTitle="Beleidsdoelen"
                links={[
                    { title: "Invoer", link: Paths.policygoal.path },
                    { title: "Dashboard", link: Paths.policygoalDashboard.path },
                ]}
            />
            {location.pathname === Paths.policygoal.path && (
                <Stack display="flex">
                    {policyGoals.map((p) => {
                        return <PolicyCard key={p.id} policy={p} />;
                    })}
                    <AddCircleIcon color="info" sx={{ fontSize: "58px", ml: "auto", cursor: "pointer" }} onClick={() => setShowDialog(true)} />
                    <Dialog open={showDialog} onClose={() => setShowDialog(false)}>
                        <DialogTitle>Creëer een beleidsdoel</DialogTitle>
                        <DialogContent>
                            <TextField label="Naam" name="name" value={dataPolicy.name} onChange={handleInputChange} fullWidth margin="normal" />
                            <TextField
                                label="Eigenschap"
                                name="characteristic"
                                value={dataPolicy.data.characteristic}
                                onChange={handleInputChange}
                                fullWidth
                                margin="normal"
                            />
                            <TextField label="Doelstelling" name="goal" value={dataPolicy.data.goal} onChange={handleInputChange} fullWidth margin="normal" />
                            <TextField label="Tijd" name="time" value={dataPolicy.data.time} onChange={handleInputChange} fullWidth margin="normal" />
                            <TextField label="Geografie" name="geo" value={dataPolicy.data.geo} onChange={handleInputChange} fullWidth margin="normal" />
                            <TextField
                                label="Categorie"
                                name="category"
                                value={dataPolicy.data.category}
                                onChange={handleInputChange}
                                fullWidth
                                margin="normal"
                            />
                            <Button
                                fullWidth
                                variant="contained"
                                color="primary"
                                onClick={() => {
                                    setShowDialog(false);
                                }}
                                sx={{ ml: "auto" }}
                            >
                                Opslaan
                            </Button>
                        </DialogContent>
                    </Dialog>
                </Stack>
            )}
            {location.pathname === Paths.policygoalDashboard.path && <>Dashboard visual will be here</>}
        </Stack>
    );
};
