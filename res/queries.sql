SELECT c.COMPONENTID, c.CONTRIBUTORID, c.CONTRIBUTION,
    ( 
      CASE COMPONENTID 
      WHEN @curType 
      THEN @curRow := @curRow + 1 
      ELSE @curRow := 1 AND @curType := COMPONENTID END
    ) AS rank
FROM ownership.CONTRIBUTIONDATA c, (SELECT @curRow := 1, @curType := '') r
ORDER BY COMPONENTID, CONTRIBUTION DESC;
