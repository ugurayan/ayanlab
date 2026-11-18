# Schedule Data Storage (Step 3)

This document describes how to persist the Step 3 schedule data in a database or mock.

## Form fields (JobFormData)

| Field | Type | Recurring | Non-recurring | Description |
|-------|------|-----------|---------------|-------------|
| `recurringSchedule` | boolean | ✓ | ✓ | Whether the schedule repeats weekly |
| `scheduleName` | string | optional | optional | e.g. "Weekly Morning Shift" |
| `selectedDates` | string[] | — | ✓ | ISO date strings (YYYY-MM-DD) for explicit dates |
| `recurringStartDate` | string | ✓ | — | Range start (YYYY-MM-DD) |
| `recurringEndDate` | string | ✓ | — | Range end (YYYY-MM-DD) |
| `shiftDays` | string[] | ✓ | — | Weekday ids: `sun`, `mon`, `tue`, `wed`, `thu`, `fri`, `sat` |
| `scheduleStartTime` | string | ✓ | ✓ | Time (HH:mm, 24h) |
| `scheduleEndTime` | string | ✓ | ✓ | Time (HH:mm, 24h) |
| `totalUnpaidBreak` | string | ✓ | ✓ | One of: "0 min", "15 min", "30 min", "45 min", "60 min" |
| `preferSamePros` | boolean | ✓ | ✓ | Prefer same staff across dates |
| `employeesNeeded` | string | ✓ | ✓ | Number (min 1) |
| `wageType` | "hourly" \| "daily" | ✓ | ✓ | |
| `wageAmount` | string | ✓ | ✓ | Decimal |

## Database storage options

### Option A: Single JSON column

Store the whole schedule in one JSON/JSONB column on the job table:

```sql
-- Example (PostgreSQL)
ALTER TABLE jobs ADD COLUMN schedule JSONB;

-- Example row
{
  "recurring": true,
  "scheduleName": "Weekly Morning Shift",
  "recurringStartDate": "2026-02-01",
  "recurringEndDate": "2026-02-28",
  "shiftDays": ["sun", "mon", "tue"],
  "startTime": "08:30",
  "endTime": "15:30",
  "totalUnpaidBreakMinutes": 30,
  "preferSamePros": false,
  "employeesNeeded": 2,
  "wageType": "hourly",
  "wageAmount": 25.50
}
```

For non-recurring, store `selectedDates` instead of `recurringStartDate` / `recurringEndDate` / `shiftDays`:

```json
{
  "recurring": false,
  "selectedDates": ["2026-02-10", "2026-02-12", "2026-02-13"],
  "startTime": "09:00",
  "endTime": "17:45",
  ...
}
```

### Option B: Normalized tables

**job_schedules** (one row per job):

| Column | Type | Description |
|--------|------|-------------|
| job_id | UUID | FK to jobs |
| recurring | boolean | |
| schedule_name | varchar | nullable |
| start_time | time | |
| end_time | time | |
| total_unpaid_break_minutes | int | 0, 15, 30, 45, 60 |
| prefer_same_pros | boolean | |
| employees_needed | int | |
| wage_type | varchar | 'hourly' \| 'daily' |
| wage_amount | decimal | |

**job_schedule_dates** (non-recurring: one row per selected date):

| Column | Type |
|--------|------|
| job_id | UUID |
| schedule_date | date |

**job_schedule_recurrence** (recurring: one row per job when recurring = true):

| Column | Type |
|--------|------|
| job_id | UUID |
| start_date | date |
| end_date | date |
| shift_days | varchar[] or JSON | e.g. ["sun","mon","tue"] |

### Option C: Mock (in-memory / localStorage)

- **Wizard:** Data is already stored in `JobFormData` and persisted to localStorage under `job-creation-wizard-data` (with 1-day expiry).
- **Submit:** On final step, send the same shape to your API (e.g. `POST /api/v1/jobs` with a `schedule` object as above). The backend can then write to DB using Option A or B.

## Mapping form → API/DB

- `totalUnpaidBreak` ("30 min") → store as integer `totalUnpaidBreakMinutes` (e.g. 30).
- `shiftDays` → keep as string array or store as comma-separated or bitmask (e.g. Sun=1, Mon=2, …).
- Times: keep as "HH:mm" or convert to ISO time / minutes-from-midnight per your API.

## Validations (already implemented in wizard)

- **Recurring:** `recurringStartDate`, `recurringEndDate` required; `end >= start`; at least one `shiftDays`.
- **Non-recurring:** at least one `selectedDates`.
- **Both:** `scheduleStartTime`, `scheduleEndTime` required; end time > start time; `totalUnpaidBreak` one of the allowed values; `employeesNeeded` ≥ 1; `wageAmount` > 0.
