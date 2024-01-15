# vng

## Setup development environment

TBD

## Deploy on production

TBD

## Development

### How to query the tables for a specific 'peildatum'/reference date

For querying for the current date you can just check if the end date of the table is `NULL`. e.g:

```sql
SELECT
    *
FROM
    diwi_testset_simplified.gemeente_state gs
WHERE
    gs.change_end_date IS NULL
```

For a specific date the query is more complex. There can not be any overlapping state/changelog entries:

```sql
SELECT
    *
FROM
    diwi_testset_simplified.gemeente_state gs
WHERE
    gs.change_start_date <= :reference_date AND (gs.change_end_date IS NULL OR gs.change_end_date > :reference_date)
```

## Glossary

As the project is meant for Dutch municipalities there are lots of Dutch terms.

| Dutch     | English        |
| --------- | -------------- |
| Peildatum | Reference date |
| Gemeente  | Municipality   |
| Buurt     | Neighbourhood  |
