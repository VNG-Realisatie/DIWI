SELECT
    json_agg(ws)
FROM
    diwi_testset_simplified.wijk_state ws
WHERE
    ws.change_end_date IS NULL
