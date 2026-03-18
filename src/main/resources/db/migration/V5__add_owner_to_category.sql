-- V5__add_owner_to_category.sql
-- Adds owner_id to category and makes it per-user.

-- 1) Add column (nullable first)
ALTER TABLE category
ADD COLUMN IF NOT EXISTS owner_id BIGINT;

-- 2) Backfill owner_id using tasks relationship (best effort)
-- If category was used by tasks, assign owner_id from task.owner_id
UPDATE category c
SET owner_id = t.owner_id
FROM task t
WHERE t.category_id = c.id
  AND c.owner_id IS NULL;

-- 3) If some categories still have NULL owner_id, assign them to the earliest user (dev-friendly fallback)
UPDATE category
SET owner_id = (SELECT id FROM app_user ORDER BY id ASC LIMIT 1)
WHERE owner_id IS NULL;

-- 4) Make column NOT NULL
ALTER TABLE category
ALTER COLUMN owner_id SET NOT NULL;

-- 5) Add FK constraint (name can be changed if you want)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_category_owner'
    ) THEN
        ALTER TABLE category
        ADD CONSTRAINT fk_category_owner
        FOREIGN KEY (owner_id) REFERENCES app_user(id);
    END IF;
END $$;

-- 6) Drop old unique constraint on category.name if it exists (constraint name may differ)
-- If you know the exact constraint name, replace it here for a cleaner migration.
DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    SELECT conname INTO constraint_name
    FROM pg_constraint
    WHERE conrelid = 'category'::regclass
      AND contype = 'u'
      AND pg_get_constraintdef(oid) LIKE '%(name)%'
    LIMIT 1;

    IF constraint_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE category DROP CONSTRAINT %I', constraint_name);
    END IF;
END $$;

-- 7) Add composite unique (owner_id, name)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uk_category_owner_name'
    ) THEN
        ALTER TABLE category
        ADD CONSTRAINT uk_category_owner_name UNIQUE (owner_id, name);
    END IF;
END $$;

-- 8) Index for performance
CREATE INDEX IF NOT EXISTS idx_category_owner_id ON category(owner_id);
