import { Box, Stack, TextField, Typography } from "@mui/material";

export const BlockHousesForm = (props: any) => {
  return (
    <Box mt={4}>
      <Typography variant="h6" fontWeight="600">
        Vul de huizenblokken in
      </Typography>
      <Typography variant="subtitle1" fontWeight="500">
        Huizenblok 1
      </Typography>
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <TextField
          id="houseblock"
          size="small"
          variant="outlined"
          value={
            props.createProjectForm ? props.createProjectForm.houseBlock : ""
          }
          onChange={(e) =>
            props.setCreateProjectForm({
              ...props.createProjectForm,
              houseBlock: e.target.value,
            })
          }
          fullWidth
        />
      </Stack>
      <Typography variant="subtitle1" fontWeight="500">
        Nader te bepalen*
      </Typography>
      <TextField
        id="bepalen1"
        size="small"
        variant="outlined"
        value={props.createProjectForm ? props.createProjectForm.bepalen1 : ""}
        onChange={(e) =>
          props.setCreateProjectForm({
            ...props.createProjectForm,
            bepalen1: e.target.value,
          })
        }
        fullWidth
      />
      <Typography variant="subtitle1" fontWeight="500">
        Nader te bepalen*
      </Typography>
      <TextField
        id="bepalen2"
        size="small"
        variant="outlined"
        value={props.createProjectForm ? props.createProjectForm.bepalen2 : ""}
        onChange={(e) =>
          props.setCreateProjectForm({
            ...props.createProjectForm,
            bepalen2: e.target.value,
          })
        }
        fullWidth
      />
      <Typography variant="subtitle1" fontWeight="500">
        Nader te bepalen*
      </Typography>
      <TextField
        id="bepalen3"
        size="small"
        variant="outlined"
        value={props.createProjectForm ? props.createProjectForm.bepalen3 : ""}
        onChange={(e) =>
          props.setCreateProjectForm({
            ...props.createProjectForm,
            bepalen3: e.target.value,
          })
        }
        fullWidth
      />
    </Box>
  );
};
