import { Box, InputLabel, MenuItem, Select, Stack, TextField, Typography } from "@mui/material";
import mapform from "../assets/temp/formmap.png";
import wijk from "../api/json/wijk.json"
export const SelectFromMapForm = (props: any) => {
  return (
    <Box mt={4} position="relative">
      <Typography variant="h6" fontWeight="600">
        Teken het project in op de kaart
      </Typography>

      <Box
        sx={{
          backgroundColor: "#FFFFFF",
          width: "30%",
          position: "absolute",
          left: 15,
          top: 45,
        }}
        p={2}
      >
        <Typography variant="subtitle1" fontWeight="500">
          Regio{" "}
        </Typography>
        <Stack
          direction="row"
          alignItems="center"
          justifyContent="space-between"
        >
          <TextField
            id="regio"
            size="small"
            variant="outlined"
            value={props.createProjectForm ? props.createProjectForm.regio : ""}
            onChange={(e) =>
              props.setCreateProjectForm({
                ...props.createProjectForm,
                regio: e.target.value,
              })
            }
            fullWidth
          />
        </Stack>
        <Typography variant="subtitle1" fontWeight="500">
          Buurt
        </Typography>
        <TextField
          id="buurt"
          size="small"
          variant="outlined"
          value={props.createProjectForm ? props.createProjectForm.buurt : ""}
          onChange={(e) =>
            props.setCreateProjectForm({
              ...props.createProjectForm,
              buurt: e.target.value,
            })
          }
          fullWidth
        />
            <Stack>
                            <InputLabel id="wijk">
                                Wijk
                            </InputLabel>
                            <Select
                                sx={{ width: "100%" }}
                                labelId="wijk"
                                id="fase"
                                value={
                                    props.createProjectForm
                                        ? props.createProjectForm.wijk
                                        : ""
                                }
                                label="Project Fase"
                                onChange={(e) =>
                                    props.setCreateProjectForm({
                                        ...props.createProjectForm,
                                        wijk: e.target.value,
                                    })
                                }
                            >
                                {wijk.map((m) => {
                                    return (
                                        <MenuItem key={m.ID} value={m.waarde_label}>
                                            {m.waarde_label}
                                        </MenuItem>
                                    );
                                })}
                            </Select>
                        </Stack>
      </Box>
      <img src={mapform} alt="mapform" width="100%"></img>
    </Box>
  );
};
