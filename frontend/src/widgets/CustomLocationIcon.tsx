import React from 'react';
import { Box } from '@mui/system';
type Props={
    bgColor:string,
}
const LocationIcon = ({bgColor}:Props) => {
  return (
    <Box
      sx={{
        width: '30px',
        height: '30px',
        backgroundColor: bgColor,
        borderRadius: '50%',
        position: 'relative',
        '&:before, &:after': {
          content: '""',
          position: 'absolute',
          width: '10px',
          height: '10px',
          backgroundColor: '#fff',
          borderRadius: '50%',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
        },
        '&:after': {
          animation: 'pulse 1s infinite',
        },
        '@keyframes pulse': {
          '0%': {
            transform: 'translate(-50%, -50%) scale(1)',
            opacity: 0.8,
          },
          '50%': {
            transform: 'translate(-50%, -50%) scale(1.2)',
            opacity: 0.4,
          },
          '100%': {
            transform: 'translate(-50%, -50%) scale(1)',
            opacity: 0.8,
          },
        },
      }}
    />
  );
};

export default LocationIcon;
