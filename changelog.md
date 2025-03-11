# Change log

## DIWI RELEASE 1.11.0

### ADDED

- Added displaying of errors in regard to missing mandatory entries to the export page and have the UI jump to the errors section for clarity
- Added authentication via oauth to PZH export page

### CHANGED

- Filters on export pages now correspond to the export's minimum confidentiality level

### FIXED

- Fixed an issue where deleted custom properties and deleted custom categories would be persisted in the project
- Fixed a bug where the scroll bar would not appear on policy goals page when browser is custom sized

### UPDATED

- Updated deprecated packages

## DIWI RELEASE 1.10.0

This release focuses on exporting data for other purposes than provincial monitors.

### ADDED

- Added alert for validation errors on 'Export Instellingen'
- Added front end validation for 'soort' and 'aantal' in houseblock ownership section
- Added an Excel export template

### CHANGED

- Different export types now have different minimal confidentiality settings. Exports that are by nature intended for external parties have a high minimum confidentiality, exports for internal use have a low minimum confidentiality
- Changes to the way the front end keeps track of custom properties
- Changes to pagination and filtering on tables

### FIXED

- Addressed a recurring issue where block data like end date would not display the new value immediately after saving
- Addressed an issue that touches on the previous one, where updated/changed property values would not display correctly until after a page refresh
- Fixed a dashboards display issue, where when switching from a valid project to an invalid project, the UI would display the values for the valid project
- Fixed an issue where confidentiality settings server side would prevent exporting that should be possible
- Fixed an issue where project order on tables was not fixed while browsing through the table's pages

### IMPROVED

- There are superfluous fetches of endpoints occuring on multiple pages. A start has been made on removing the unnescessary ones, this is ongoing

## DIWI RELEASE 1.9.0

## ADDED

- Policy goals are now selectable by category in blueprints section
- Added a page with the audit trail within each individual project
- Added a Geojson export template

### CHANGED

- Projects table was refactored, we are now using a component that can be used independantly over multiple pages
- Changed the milestone proces to clean up old and unused milestones, as this could cause conflicts when changing names and dates multiple times

### FIXED

- Fixed typo's in importer error messages
- Fixed an issue where missing project phases from imports would be shown in the dashboard as a separate category
- Fixed a typo in the user group pop up
- Fixed a bug where changing project and block dates repeatedly back and forth would change the ownership ID, which should be stable in this instance
- Fixed an issue whith coordinate system mismatches, causing plots to end up in the Siberian Sea, which in turn caused an issue that would not let the user correct the plot
- Fixed an issue where imported files were stored in the wrong folder within DIWI, hence not being retrievable
- Fixed a bug where the dashboard PDF would only show one policy goal per policy goal category

## DIWI RELEASE 1.8.1

### CHANGED

- The plot will no longer be coloured/accentuated/highlighted when a subplot has been created

### FIXED

- Addressed an issue where the projects table would not filter on plantype, planstatus, gemeente, wijk, buurt
- Fixed speeling mistakes in export page xD
- Addressed a situation where the untranslated name would be used for a property in the goals page
- Fixed an issue where the table on the export page would tell you there was a certain amount of lines being displayed, whilst displaying a different amount
- Fixed the issue on the export page where selecting projects to export over multiple pages would yield unexpected results: some projects would not be exported
- Fixed a bug where adding categories to a custom categorical property would override/reset the selections for mandatory and single select, after creation of the property
- Fixed the issue where imported geometry was retained yet not displayed, as an overlay or otherwise

### REMOVED

- Removed the API key and URL boxes from the export settings. They are non-functional right now and may just create confusion. These should be reintroduced after gaining more clarity on the requirements for exporting

## DIWI RELEASE 1.8.0

This release is all about data exchenge with a main focus on export to Zuid-Holland provincie monitor and a side quest of making imports a lot easier

### ADDED

- Added export functionality for Zuid-Holland provincie monitor to data exchange. The export creates a geojson that it can either send to recipient API or store locally. The back end enforces minimum confidentiality level for exports.
- Added 'Export instellingen' page where exporting can be configured
- Required property options added to endpoint
- Added creation options in Import page for missing properties and categories
- Projects table is now configureable. The settings will not be hard saved in the backend, but stored in a cookie locally, this means the settings will revert to default on each session
- Added bulk editing for confidentiality level
- Added back end and front end validation to the export page
- Added filter options to export page
- Added confidentiality validation to export page
- Added feedback concerning export errors to the UI

### CHANGED

- Moved Import page under 'Data uitwisseling' header
- Data exchange page now contains an import section, an export section and a link to the setup page for geojson exports
- Required property options added to endpoint
- Required property options added to data base
- Price ranges now accept int8, instead of int4 (64 bit vs 32 bit) allowing for larger numbers, now well exceeding the range of the plausible
- Custom properties can now be marked as mandatory and/or single select
- Changed some functionality in the backend so it can handle a some category types as category or text when exporting

### FIXED

- Addressed an issue where the legend of a dashboard graph would go out of bounds in cases with large numbers of projects
- More user access issues fixed, the back end enforces more now
- Fixed an error generating an error pop up on log out
- Addressed an issue where selection would not work cross page (max page length being 100 and projects to be selected able to exceed this number)
- Fixed an issue where amounts would not be displayed in the projects table
- Fixed a recurring bug where previously deleted categories for properties were nonetheless displayed in selectors

## DIWI RELEASE 1.7.1

### FIXED

- Fixed a bug where not all users were fetched in the custom dashboards wizard

## DIWI RELEASE 1.7.0

### ADDED

- Policy goals have been implemented. They are editable and viewable through the dashboard section
- Subplots feature is now implemented on the map
- piecharts for purchase and rent stats and a table for planning have been added to the dashboard

### CHANGED

- Houseblocks can now have a value of 0
- Export page is hidden in the menu, until it is ready for release
- The controls for plot selection have been slightly altered to accomodate the new features better

### FIXED

- Fixed an issue where sorting and filtering on wijk / buurt did not work
- There was an issue where UserPlus changes to plots were not persisted
- Addressed an issue where the new policy goals displays would not show the correct percentages
- Fixed an issue where the dashboard pie charts would display demolition in positive numbers
- Addressed situations where certain user types had access to pages that should not be accessible and/or hidden
- fixed an issue in the pdf where an odd number of items would cause the goals to look wonky
- Addressed display issues in graphs, ranging from bar widths to data used for display

## DIWI RELEASE 1.6.3

### FIXED

- fixed an error in the migration script to support really large values in the price categories

## DIWI RELEASE 1.6.2

### FIXED

- fixed an error in the migration script to fix the price categories

## DIWI RELEASE 1.6.1

### ADDED

- added a text on the geojson import page to inform the user that it is possible to convert certain geojsons to the correct import pattern

### FIXED

- fixed an issue where the ghost of a deleted category of a custom property would prohibit the addition of a category of the same name
- fixed an issue where on the single project map page the individual block's numbers would not indicate an addition or subtraction (bouw/sloop) by adding a minus (-) to demolition blocks
- fixed an issue where ghosts of deleted fixed property categories would still be available in the project wizard
- fixed an issue where when going from one project's map location to another, the map would 'hang' on the previous location
- fixed a bug where adding amounts to houseblocks did not immediately update amounts in project
- fixed an issue where the 1.6.0 db migration caused some errors in price ranges and amounts

## DIWI RELEASE 1.6.0

This release focusses on expanding dashboard features and ranged categories.

### ADDED

- added missing or incomplete tooltips
- added a PDF export option to the dashboard pages
- added custom dashboards page: this will allow tailored dashboards for users and usergroups
- added list page for custom dashboards
- added price categories to importers
- added projectplanning bar chart, purchase pie chart and rent pie chart to dashboard

### CHANGED

- changed the handling of ownership of private projects. these can no longer be removed from the original creator
- automated end to end tests have been updated, though not fully to the current version
- Dashboard projects now retrieve data from single endpoint

### FIXED

- fixed an issue where price categories were not displaying their meaning clear enough
- fixed an issue where a price category needed additional actions to be saveable
- fixed an issue where usertype could not be changed anymore
- fixed instances where certain user types could access parts of the UI they were not supposed to access
- fixed validation issues in the projecct wizard
- fixed an issue reportes by Apeldoorn where category labels were not saveable
- fixed an issue in the dashboards where there were space issues in case of many categories
- fixed an issue where saving changes in house blocks would not give feedback
- fixed an issue where saving plot updates would not give feedback
- fixed an issue where a user that had been removed from a group would still have access to projects assigned to that group
- fixed an issue where a user assigned to a user group would not see private projects assigned to that user group
- about page git hash is showing again
- fixed issues with automated tests

### IMPROVED

- updated automated tests

## DIWI RELEASE 1.5.0

This release is focused on dashboards.

### ADDED

- implemented page and structure for the new dashboard
- added charts and statistics to dashboard page
- added map features on dashboard page

### CHANGED

- updated the sidebar with the new page

### FIXED

- fixed an issue where after saving certain categorical properties and moving on to anpother categorical property, the categories for the previous property would show
- there was an error with editing ordinal properties
- some fixed properties were not yet translated into Dutch in the edit dialog

## DIWI RELEASE 1.4.0

This release is focused on user management and project confidentiality. The user type and the project ownership and confidentialty now decide what projects are visible to a user and who can change a project.

### ADDED

- User management
- User group mangement
- User types
- Project confidentiality and ownership now influences who can see a project
- Automated end to end tests

### CHANGED

- Use vite js as front end builder for increased build speed
- Make optional input widgets clearable

### REMOVED

### FIXED

- Sorting on project page now works for district, neighborhood and municpality

## DIWI RELEASE 1.3.3

### FIXED

- Fixed an issue with updating an existing house block in a project.

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
