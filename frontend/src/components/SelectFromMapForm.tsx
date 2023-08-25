import { Box, Stack, TextField, Typography } from "@mui/material";
import mapform from "../assets/temp/formmap.png";
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
          Kern
        </Typography>
        <TextField
          id="kern"
          size="small"
          variant="outlined"
          value={props.createProjectForm ? props.createProjectForm.kern : ""}
          onChange={(e) =>
            props.setCreateProjectForm({
              ...props.createProjectForm,
              kern: e.target.value,
            })
          }
          fullWidth
        />
        <Typography variant="subtitle1" fontWeight="500">
          Wijk
        </Typography>
        <TextField
          id="wijk"
          size="small"
          variant="outlined"
          value={props.createProjectForm ? props.createProjectForm.wijk : ""}
          onChange={(e) =>
            props.setCreateProjectForm({
              ...props.createProjectForm,
              wijk: e.target.value,
            })
          }
          fullWidth
        />
      </Box>
      <img src={mapform} alt="mapform" width="100%"></img>
    </Box>
  );
};
