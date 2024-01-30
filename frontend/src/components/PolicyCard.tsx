import { Grid, MenuItem, Select, Stack, TextField, Typography } from "@mui/material";
import { useState } from "react";
import { Policy } from "../api/dummyData";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import percentageOption from "../api/json/enums/doel_richting.json";
import { useTranslation } from "react-i18next";

export const PolicyCard = ({ policy }: Policy) => {
    const [editable, setEditable] = useState(false);
    const [percentageActive, setPercentageActive] = useState(false);
    const [editedName, setEditedName] = useState(policy.name);
    const [editedCharacteristic, setEditedCharacteristic] = useState(policy.data.characteristic);
    const [editedGoal, setEditedGoal] = useState(policy.data.goal);
    const [editedTime, setEditedTime] = useState(policy.data.time);
    const [editedGeo, setEditedGeo] = useState(policy.data.geo);
    const [editedPercentage, setEditedPercentage] = useState(policy.data.doel_richting);
    const [editedCategory, setEditedCategory] = useState(policy.data.category);

    const{t}=useTranslation();
    const characteristicActiveStyle = {
        backgroundColor: "#002C64",
        color: "#FFFFFF",
        borderRadius: percentageActive ? "0px 3px 3px 0px" : "3px 0px 0px 3px",
        cursor: "pointer",
    };
    const characteristicPassiveStyle = { cursor: "pointer" };

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        if (name === "editedName") {
            setEditedName(value);
        } else if (name === "editedcharacteristic") {
            setEditedCharacteristic(value);
        } else if (name === "goal") {
            setEditedGoal(value);
        } else if (name === "doel_richting") {
            setEditedPercentage(value);
        } else if (name === "time") {
            setEditedTime(value);
        } else if (name === "geo") {
            setEditedGeo(value);
        } else if (name === "category") {
            setEditedCategory(value);
        }
    };

    return (
        <Grid container my={2}>
            {!editable && (
                <Grid item sm={12} sx={{ backgroundColor: "#738092", color: "#FFFFFF", p: 1 }} display="flex" justifyContent="space-between">
                    {t("policy.name")} {policy.name}
                    <EditIcon sx={{ cursor: "pointer" }} onClick={() => setEditable(true)} />
                </Grid>
            )}
            {editable && (
                <Grid item sm={12} sx={{ p: 1 }} display="flex" justifyContent="space-between">
                    <TextField fullWidth color="secondary" value={editedName} name="editedName" onChange={handleInputChange} variant="standard" />
                    <SaveIcon
                        sx={{ cursor: "pointer" }}
                        onClick={() => setEditable(false)} //ToDo add save action later update policyname in parent
                    />
                </Grid>
            )}
            <Grid item sm={2}>
                <Typography sx={{ border: "solid 1px #ddd", p: 0.6 }}>{t("policy.characteristic")}</Typography>
                {!editable && <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{policy.data.characteristic}</Typography>}
                {editable && (
                    <TextField color="secondary" value={editedCharacteristic} name="editedcharacteristic" onChange={handleInputChange} variant="standard" />
                )}
            </Grid>
            <Grid item sm={3}>
                <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ border: "solid 1px #ddd", p: 0.5 }}>
                    <Typography>{t("policy.goal")}</Typography>
                    <Stack direction="row" border="solid 1px #ddd" borderRadius="5px">
                        <Typography
                            onClick={() => setPercentageActive(false)}
                            fontSize={13}
                            sx={percentageActive ? characteristicPassiveStyle : characteristicActiveStyle}
                            p={0.3}
                        >
                            {t("policy.number")}
                        </Typography>
                        <Typography
                            onClick={() => setPercentageActive(true)}
                            fontSize={13}
                            p={0.3}
                            sx={percentageActive ? characteristicActiveStyle : characteristicPassiveStyle}
                        >
                            {t("policy.percentage")}
                        </Typography>
                    </Stack>
                </Stack>
                {!editable && <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{policy.data.goal}</Typography>}
                {editable &&
                    (!percentageActive ? (
                        <TextField sx={{ width: "95%" }} color="secondary" value={editedGoal} name="goal" onChange={handleInputChange} variant="standard" />
                    ) : (
                        <Stack direction="row">
                            <Select
                                sx={{ width: "50%" }}
                                labelId="mutatiesoort"
                                id="fase"
                                name="doel_richting"
                                size="small"
                                value={editedPercentage}
                                variant="standard"
                                //@ts-ignore
                                onChange={handleInputChange}
                            >
                                {percentageOption.map((m) => {
                                    return (
                                        <MenuItem key={m} value={m}>
                                            {m}
                                        </MenuItem>
                                    );
                                })}
                            </Select>
                            <TextField sx={{ width: "50%" }} color="secondary" value={editedGoal} name="goal" onChange={handleInputChange} variant="standard" />
                        </Stack>
                    ))}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={{ border: "solid 1px #ddd", p: 0.6 }}>{t("policy.time")}</Typography>
                {!editable && <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{policy.data.time}</Typography>}
                {editable && <TextField color="secondary" value={editedTime} name="time" onChange={handleInputChange} variant="standard" />}
            </Grid>
            <Grid item sm={2}>
                <Typography sx={{ border: "solid 1px #ddd", p: 0.6 }}>{t("policy.geography")}</Typography>
                {!editable && <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{policy.data.geo}</Typography>}
                {editable && <TextField color="secondary" value={editedGeo} name="geo" onChange={handleInputChange} variant="standard" />}
            </Grid>
            <Grid item sm={3}>
                <Typography sx={{ border: "solid 1px #ddd", p: 0.6 }}>{t("policy.category")}</Typography>
                {!editable && <Typography sx={{ border: "solid 1px #ddd", p: 0.5 }}>{policy.data.category}</Typography>}
                {editable && <TextField fullWidth color="secondary" value={editedCategory} name="category" onChange={handleInputChange} variant="standard" />}
            </Grid>
        </Grid>
    );
};
