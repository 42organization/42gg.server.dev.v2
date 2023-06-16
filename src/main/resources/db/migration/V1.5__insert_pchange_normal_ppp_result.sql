UPDATE pchange p
SET ppp_result = COALESCE(
    (
        SELECT subquery.ppp_result
        FROM (
            SELECT p2.ppp_result
            FROM pchange p2
            INNER JOIN game AS g ON p.game_id = g.id
            WHERE p.user_id = p2.user_id
                AND g.season_id = (SELECT season_id FROM game WHERE id = p.game_id)
                AND p2.created_at < p.created_at
                AND p2.ppp_result != 0
            ORDER BY p2.created_at DESC
            LIMIT 1
        ) AS subquery
    ),
    1000
)
WHERE ppp_result = 0 OR ppp_result IS NULL;
