import React from 'react';
import { Button, Grid } from '@mui/material';
import TextInput from '../project/inputs/TextInput';
import CategoryInput from '../project/inputs/CategoryInput';
import { User } from '../../pages/UserManagement';

type CustomDashboardProps = {
  newBlueprint: {
    name: string;
    users: any[];
  };
  setNewBlueprint: (blueprint: any) => void;
  users: User[];
};

export const CustomDashboardForm = ({ newBlueprint, setNewBlueprint, users } : CustomDashboardProps) => {
  return (
    <Grid container spacing={2} alignItems="center">
      <Grid item xs={4}>
        <TextInput
          readOnly={false}
          value={newBlueprint.name}
          setValue={(event: React.ChangeEvent<HTMLInputElement>) => {
            setNewBlueprint({ ...newBlueprint, name: event.target.value });
          }}
          mandatory={true}
          errorText="Please enter a group name" // Replace with t("admin.userManagement.errors.groupName") for i18n
        />
      </Grid>
      <Grid item xs={4}>
        <CategoryInput
          readOnly={false}
          mandatory={true}
          options={users ?? []}
          values={newBlueprint.users}
          setValue={(_, newValue) => {
            if (!newValue) {
                setNewBlueprint({ ...newBlueprint, users: [] });
            } else if (Array.isArray(newValue)) {
              const transformedUsers = newValue.map((user) => ({
                uuid: user.uuid || user.id,
                firstName: user.firstName,
                lastName: user.lastName,
              }));
              setNewBlueprint({ ...newBlueprint, users: transformedUsers });
            }
          }}
          multiple={true}
          error="Please add at least one member" // Replace with t("admin.userManagement.errors.addMember") for i18n
        />
      </Grid>
      <Grid item xs={4}>
        <Button variant="contained" onClick={() => console.log('Save Clicked')}>
          Save
        </Button>
      </Grid>
    </Grid>
  );
};
