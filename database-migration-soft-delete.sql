-- Migration Script for Course Content Soft Delete Feature
-- Date: 2025-10-27
-- Description: Adds soft delete support to course_content table

-- Add soft delete columns to course_content table
ALTER TABLE course_content
ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE course_content
ADD COLUMN deleted_at TIMESTAMP NULL;

-- Create index on is_deleted for faster queries
CREATE INDEX idx_course_content_is_deleted
ON course_content(is_deleted);

-- Create composite index for frequently queried combinations
CREATE INDEX idx_course_content_course_not_deleted
ON course_content(course_id, is_deleted);

CREATE INDEX idx_course_content_user_not_deleted
ON course_content(uploaded_by, is_deleted);

-- Verify the changes
SELECT
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'course_content'
  AND column_name IN ('is_deleted', 'deleted_at')
ORDER BY ordinal_position;

-- Sample query to test soft delete functionality
-- This should return only non-deleted content for a course
-- SELECT * FROM course_content WHERE course_id = 1 AND is_deleted = FALSE;

COMMIT;

