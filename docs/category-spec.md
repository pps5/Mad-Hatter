# Category Specification

## Overview
The category domain model represents a labeled classification for transactions with optional two-level hierarchy (category -> subcategory). Categories are tagged as income or expense types and can be user-defined or preset.

## Fields

| Field | Required | Type | Description | Validation |
| --- | --- | --- | --- | --- |
| `id` | Required | Long | Unique identifier for the category. | Must be greater than 0. |
| `name` | Required | String | Display name of the category. | Must be non-blank. |
| `type` | Required | Enum (`INCOME`, `EXPENSE`) | Transaction type this category applies to. | Must be one of the supported enum values. |
| `isPreset` | Required | Boolean | Marks a category as preset so the frontend can localize the display name independently of the stored `name`. | No validation rules beyond presence. |
| `isActive` | Optional | Boolean | Indicates whether the category is active. Defaults to `true` when omitted. | No validation rules beyond presence. |
| `parentId` | Optional | Long | Parent category identifier for a subcategory. When `null`, the category is a top-level category. | When present, must be greater than 0. |

## Hierarchy
Categories support a two-level hierarchy via `parentId`. A top-level category has `parentId = null`, and a subcategory references its parent category ID. The model does not support deeper nesting.

## Example Data

```json
{
  "id": 101,
  "name": "Groceries",
  "type": "EXPENSE",
  "isPreset": true,
  "isActive": true,
  "parentId": null
}
```

```json
{
  "id": 102,
  "name": "Farmers Market",
  "type": "EXPENSE",
  "isPreset": false,
  "isActive": true,
  "parentId": 101
}
```

## API
No category API endpoints are defined in the current codebase.
