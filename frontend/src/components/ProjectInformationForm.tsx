import { Box, Stack, TextField, Typography } from "@mui/material";
import ColorSelector from "./ColorSelector";

export const ProjectInformationForm = (props: any) => {
  //ToDo add props later
  const handleColorChange = (newColor: string) => {
    console.log("Selected color:", newColor);
    // You can perform any action with the selected color here
    props.setCreateProjectForm({ ...props.createProjectForm, color: newColor });
  };
  return (
    <Box mt={4} >
      <Typography variant="h6" fontWeight="600">
        Vul de projectgegevens in
      </Typography>
      <Typography variant="subtitle1" fontWeight="500">
        Projectnaam en projectkleur*
      </Typography>
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <TextField
          id="projectname"
          size="small"
          variant="outlined"
          value={
            props.createProjectForm ? props.createProjectForm.projectName : ""
          }
          sx={{ width: "97%" }}
          onChange={(e) =>
            props.setCreateProjectForm({
              ...props.createProjectForm,
              projectName: e.target.value,
            })
          }
        />
        <ColorSelector
          selectedColor={props.createProjectForm}
          defaultColor="rgba(255, 87, 51, 1)"
          onColorChange={handleColorChange}
        />
      </Stack>
      <Typography variant="subtitle1" fontWeight="500">
        Geografie*
      </Typography>
      <TextField
        id="geo"
        size="small"
        variant="outlined"
        value={props.createProjectForm ? props.createProjectForm.geo : ""}
        onChange={(e) =>
          props.setCreateProjectForm({
            ...props.createProjectForm,
            geo: e.target.value,
          })
        }
        fullWidth
      />
      <Typography variant="subtitle1" fontWeight="500">
        Organisatie*
      </Typography>
      <TextField
        id="organization"
        size="small"
        variant="outlined"
        value={
          props.createProjectForm ? props.createProjectForm.organization : ""
        }
        onChange={(e) =>
          props.setCreateProjectForm({
            ...props.createProjectForm,
            organization: e.target.value,
          })
        }
        fullWidth
      />
      <Typography variant="subtitle1" fontWeight="500">
        Woningen en aantallen*
      </Typography>
      <TextField
        id="Homesandnumbers"
        size="small"
        variant="outlined"
        value={
          props.createProjectForm ? props.createProjectForm.homesInformation : ""
        }
        onChange={(e) =>
          props.setCreateProjectForm({
            ...props.createProjectForm,
            homesInformation: e.target.value,
          })
        }
        fullWidth
      />
    </Box>
  );
};
