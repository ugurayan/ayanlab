# Dashboard dynamic widgets

## 1. Widget format and DB design

### Widget catalog (reference data)

Stored as config or `widget_catalog` table:

| Column        | Type    | Description                                      |
|---------------|---------|--------------------------------------------------|
| id            | string  | Unique widget ID (e.g. `recent-shifts`)         |
| name          | string  | Display name                                     |
| description   | string? | Short description                                |
| roles         | string[]| Role IDs that can use this widget (main role UUIDs) |
| default_order | int     | Order in default layout                          |
| default_w     | int     | Default grid width                               |
| default_h     | int     | Default grid height                              |

### Default layout per role

Table `dashboard_defaults` or config:

| Column     | Type     | Description                |
|------------|----------|----------------------------|
| role_id    | string   | Main role UUID (PK)        |
| widget_ids | string[] | Ordered list of widget IDs |

One row per role; when user has no saved config, this defines the default widgets and order.

### User customization (by profileId)

Table `dashboard_config`:

| Column     | Type      | Description                          |
|------------|-----------|--------------------------------------|
| profile_id | string    | User profile ID (PK)                 |
| widget_ids | string[]  | Ordered list of widget IDs to show   |
| layout     | json?     | Optional grid positions per widget   |
| updated_at | timestamp | Last save time                       |

- **layout** (optional): `[{ widgetId, x, y, w, h }, ...]` for drag-and-drop grid.
- If no row exists for a `profile_id`, the app uses default layout for the user’s role.

---

## 2. API request/response (mock and real)

### GET /api/v1/dashboard/widgets

**Query:** `roleId` (optional) – if present, return only widgets available for that role.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "worker-total-shifts",
      "name": "Total shifts",
      "description": "Completed and upcoming shifts",
      "roles": ["9c2b8be8-bdec-4484-96b6-a2145b654b4d"],
      "defaultOrder": 100,
      "defaultSize": { "w": 1, "h": 1 }
    }
  ]
}
```

---

### GET /api/v1/dashboard/defaults

**Query:** `roleId` (required).

**Response:**
```json
{
  "success": true,
  "data": {
    "roleId": "9c2b8be8-bdec-4484-96b6-a2145b654b4d",
    "widgetIds": ["worker-total-shifts", "worker-applied-jobs", "worker-payments", "worker-upcoming-shifts"]
  }
}
```

---

### GET /api/v1/dashboard/config

**Query:** `profileId` (required).

**Response:** User has never saved → `data: null`. Otherwise:
```json
{
  "success": true,
  "data": {
    "profileId": "prof_abc123",
    "widgetIds": ["worker-total-shifts", "worker-applied-jobs", "worker-upcoming-shifts"],
    "layout": [
      { "widgetId": "worker-total-shifts", "x": 0, "y": 0, "w": 1, "h": 1 }
    ],
    "updatedAt": "2026-01-25T12:00:00.000Z"
  }
}
```

---

### PUT /api/v1/dashboard/config

**Body:**
```json
{
  "profileId": "prof_abc123",
  "widgetIds": ["worker-total-shifts", "worker-applied-jobs", "worker-payments"],
  "layout": []
}
```

**Response:** Same shape as `GET config` `data` (full saved config).

---

## 3. Where things live in the app

- **Types:** `src/types/dashboard.ts`
- **Widget catalog + default layouts:** `src/data/constants/dashboardWidgets.ts`
- **Mock APIs:** `src/mock/dashboardApi.ts` (in-memory store keyed by `profileId`)
- **Real API client:** `src/api/services/dashboard.ts`
- **Hooks:** `src/data/hooks/useDashboard.ts`  
  - `useDashboardWidgets(roleId)`  
  - `useDashboardDefaults(roleId)`  
  - `useDashboardConfig(profileId)`  
  - `useSaveDashboardConfig()`

To use: resolve user’s main role and `profileId`, then either load `useDashboardConfig(profileId)` and use `data.widgetIds` (and optional `data.layout`) when present, or fall back to `useDashboardDefaults(roleId).data.widgetIds`.
