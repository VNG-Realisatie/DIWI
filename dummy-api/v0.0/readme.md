# API Description:

The API is versioned, with the version being part of the URL.

This document describes the version indicated by v0.0, which is a development version in constant flux and subject to change without notice.

## Available paths:

- `/projecten`: an overview of all available projects, including basic information for a project.
- `/project/<id>`: an overview of the details for a single project, including all information for each huizenblok.

## Universal key names:

The following items specify the

- `id`: The unique identifier associated with this specific item.
- `type`: This defines the type of an item.
- `properties`: This optional value contain an arbitrary list of properties for a certain item.

The following property keys have a special value applicable to each object.

- `name`: The title for this item.
- `start_date`: a required ISO 8601 date indicating the start date of an item on the timeline. (mutually exclusive with `date`)
- `end_date`: an optional ISO 8601 date indicating the end date of an item on the timeline. (mutually exclusive with `date`)
- `date`: a required ISO 8601 date indicating a specific moment of an item on the timeline. (mutually exclusive with `start_date` and `end_date`)

## Type descriptions:

### Object types:

#### Project

Identified by `{"type": "Project", [...]}` in the JSON, this contains information about a single project.

The following optional property keys are defined for this type:

- `huizenblokken`: list of all the huizenblokken in a single project, consisting of a list of `Huizenblok` items.
- `milestones`: list of all milestones associated with a single project, consisting of a list of `Milestone` items.
- `documents`: list of all documents associated with a single project, consisting of a list of `Document` items.
- `amount`: This optional value can either be a `number` or a `Range` specifying the total amount of items in this Project. (This is a summation of the amounts for each huizenblok)
- `geometry`: This optional value contains one of the following GeoJSON objects: `Point`, `LineString`, `Polygon`, `MultiPoint`, `MultiLineString`, or `MultiPolygon`.

This object uses the `date_start` and `date_end` construct for the dates.

#### Huizenblok

Identified by `{"type": "Huizenblok", [...]}` in the JSON, this contains information about a single huizenblok.

The following optional property keys are defined for this type:

- `amount`: This optional value can either be a `number` or a `Range` specifying the total amount of items in this huizenblok.
- `geometry`: This optional value contains one of the following GeoJSON objects: `Point`, `LineString`, `Polygon`, `MultiPoint`, `MultiLineString`, or `MultiPolygon`.

This object uses the `date_start` and `date_end` construct for the dates.

#### Milestone

Identified by `{"type": "Milestone", [...]}` in the JSON, this contains information about a single milestone.

The following optional property keys are defined for this type:

[none]

This object uses the `date` construct for the dates.

#### Document

Identified by `{"type": "Document", [...]}` in the JSON, this contains information about a single document.

The following optional property keys are defined for this type:

- `description`: an optional description associated with this document.

This object uses the `date` construct for the dates.

### Property types

Properties can be added in several places, and can be expressed in multiple ways, to indicate what is known or unknown about certain numbers.

#### Specific value, applicable to all.

This is the most simple case, with a single value for the property applicable the complete set.

`<propertyName>: <propertyValue>`

#### Ranged Amount

Here we aren't certain about the exact amount of items in the set the property applies to,
but we already know the upper limit, lower limit, or both.

`<propertyName>: {<propertyValue>: {"type": "Range", minimum: <number|null>, maximum: <number|null>}, [...]}`

#### Multiple values or uncertain.

In this case, there are multiple possible values for a property, with optionally an amount of items this property applies to.

`<propertyName>: {<propertyValue>: <number|Range|null>, <propertyValue>: <number|Range|null>, [...]}`
