-- Idempotent script to rename lowercase tables in schema 'core' to TitleCase quoted names
-- Run this as a DB superuser or a role with ALTER privileges on the tables.
-- It will only rename if the lowercase table exists and the TitleCase table does not yet exist.

DO $$
DECLARE
    tbl record;
BEGIN
    -- Mapping: lowercase -> TitleCase
    FOR tbl IN SELECT * FROM (VALUES
        ('app','"App"'),
        ('applicationmodule','"ApplicationModule"'),
        ('applicationorganization','"ApplicationOrganization"'),
        ('applicationpermission','"ApplicationPermission"'),
        ('applicationrole','"ApplicationRole"'),
        ('applicationusertype','"ApplicationUserType"'),
        ('module','"Module"'),
        ('organization','"Organization"'),
        ('organizationuser','"OrganizationUser"'),
        ('permission','"Permission"'),
        ('profilepermission','"ProfilePermission"'),
        ('profilerole','"ProfileRole"'),
        ('profileusertype','"ProfileUserType"'),
        ('role','"Role"'),
        ('rolepermission','"RolePermission"'),
        ('twiliosmslog','"TwilioSmsLog"'),
        ('user','"User"'),
        ('userdetail','"UserDetail"'),
        ('userprofile','"UserProfile"'),
        ('usertype','"UserType"')
    ) AS t(lowername, titlecase)
    LOOP
        IF (SELECT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema='core' AND table_name = tbl.lowername))
           AND NOT (SELECT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema='core' AND table_name = trim(both '"' from tbl.titlecase))) THEN

            RAISE NOTICE 'Renaming core.% -> core.%', tbl.lowername, trim(both '"' from tbl.titlecase);
            EXECUTE format('ALTER TABLE core.%I RENAME TO %s', tbl.lowername, tbl.titlecase);
        ELSE
            IF (SELECT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema='core' AND table_name = tbl.lowername)) THEN
                RAISE NOTICE 'Skipping %: target % already exists', tbl.lowername, tbl.titlecase;
            END IF;
        END IF;
    END LOOP;
END$$;

-- Note:
-- - This script renames only tables present in the mapping above. Add more mappings if you have additional tables.
-- - After renaming, if your application uses Hibernate with globally_quoted_identifiers=true, it will look for the quoted TitleCase names.
-- - Back up your DB before running.
-- - To run: psql -h <host> -U <superuser> -d appdb -f platform/db/rename_tables_to_titlecase.sql

