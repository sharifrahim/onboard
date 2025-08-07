-- Add type column to approval_table
ALTER TABLE approval_table 
ADD COLUMN type VARCHAR(50) NOT NULL DEFAULT 'CREATE_COMPANY';

-- Update existing records to have the CREATE_COMPANY type
UPDATE approval_table SET type = 'CREATE_COMPANY' WHERE type = 'CREATE_COMPANY';

-- Remove the default value constraint after updating existing records
ALTER TABLE approval_table ALTER COLUMN type DROP DEFAULT;
