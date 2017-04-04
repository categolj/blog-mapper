DELETE FROM entry
WHERE entry_id IN (99997, 99998, 99999);

DELETE FROM tag
WHERE tag_name LIKE 'test%';