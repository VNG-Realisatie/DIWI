## DIWI RELEASE 1.1.3


### ADDED

- custom properties are now implemented; the UI now differentiates between fixed and custom properties.
- the log out button now actually logs the user out.
- past projects can now be displayed and updated.
- custom properties will take on the added functionality of user roles.
- delete woningblok back end functionality has been added (front end yet to be done).
- implemented ranges for 'grootte' field.


### REMOVED

- actor/role tables were removed from the data base; the user and role setup was redesigned.


### CHANGED

- filtering on Projects table has been improved.
- displayed project totals changed from net to gross.
- displayed woningblok totals in woningblok header changed from net to gross.
- housblock form was redesigned.
- house block date fields are prefilled with project start and end dates.
- filters are now retained on page refresh.
- 'Project naam' column was given a wider default for page legibility.
- duplicate custom field names will receive a numeric postfix.
- Some technical terms (e.g. boolean, numeric, etc) were replaced with more user friendly terminology.
- visual layouts were changed for a clear indication of editability/non-editability.


### FIXED

- fixed an issue where the wizard would not move on from woningblok to map.
- fixed an issue where the selected color was not retained.
- addressed an inconsistency in sizing for different custom field types.
- fixed an issue where navigating away from the wizard did not retain the filled out fields, but did save them.
- fixed a bug where a block would get added twice when pressing 'opslaan' followed by 'volgende'.
- fixed a bug where the saved date would be saved as the saved date minus 1, causing all sorts of mayhem.
- fixed an issue where the displayed project totals were not updated properly when changing amounts in blocks.
- fixed a display issue where all start and end dates would show as today().
- fixed an issue where entries from a previously added custom field were persisted.
- fixed an issue where the wizard would not move on after completing the drawing phase.
- fixed the url to incorporate the correct ID.