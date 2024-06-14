# Change log

## DIWI RELEASE 1.3.2

### ADDED

- Log first and last delivery date of house blocks during import

### FIXED

- Show correct number of houses in projects table and project map

### CHANGED

- Improved error messages when importing. e.g. show property and option names for missing custom properties.

## DIWI RELEASE 1.3.1

### ADDED

- Multi house block imports in excel importer.

### FIXED

- Fix plots of old projects not showing up on project map.
- Translations for ownership inputs were added.
- Disabled categories for physical appearance don't show up anymore.
- Clicking back in project wizard for house blocks saves the house blocks.

## DIWI RELEASE 1.3.0

This release is focused on the experimental geojson importer, which is only available for administrators at the moment, as well as a redesign of the project details and correcting naming issues.

### ADDED

- The geojson importer

### CHANGED

- Project editor has been redesigned to match project wizard.
- gemeente/buurt/wijk have been converted to be fixed properties. They always exist, but the categories are editable.

### REMOVED

- Projectleider has been removed from the detail form and the backend.
- Amount validation in 'Eigendom en waarde' segment has been removed.

### FIXED

- Changed a few instances where English, rather than Dutch, was used in the front end.
- Adressed an issue where totals were not updated on saving, but had to be refreshed.
- Fixed an issue where map editor became collapsible instead of saveable.

## DIWI RELEASE 1.2.0

Options for project properties like priority can now be configured.

Also added in this release is the experimental excel importer. Only available for administrators at the moment.

### ADDED

- the excel importer.
- blocks in a project can now be deleted.
- make categories for fixed properties editable
- added separate front end commit hash to about page to make it easier to check if you need to refresh.
- implement ordinal fixed and custom properties
- allow renaming options for fixed and custom properties

### REMOVED

- remove unused plotting library

### CHANGED

- prepare database for drawing inside of plots
- change UI to match design more closely
- remove unused map data from repository
- change default map bounds to the netherlands instead of eindhoven.
- simplified house block mutation. You can now only set demolition or construction and an amount.

### FIXED

- fixed duplicated error notifications
- fixed project not being saved when clicking next in wizard
- handle errors when saving custom properties
- lots of fixes in language and capitalization
- fix issues with end date of project changing when saving existing project again.
- fix issue with custom properties not refreshing when saving them

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
