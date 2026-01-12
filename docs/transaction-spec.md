# Transaction Specification

## Overview
The transaction domain model represents a single financial movement recorded in UTC with a positive amount and a fixed, three-letter currency code.

## Fields

| Field | Required | Type | Description | Validation |
| --- | --- | --- | --- | --- |
| `timestamp` | Required | ISO-8601 datetime (UTC) | Time the transaction occurred. Stored as `Instant` (UTC). | Must be a valid UTC timestamp. |
| `amount` | Required | Decimal | Monetary amount for the transaction. Stored as `BigDecimal`. | Must be greater than 0 and in increments of `0.01`. |
| `currencyCode` | Required | String | ISO-4217 currency code (e.g., `USD`). | Must be non-blank and match `^[A-Z]{3}$`. |
| `type` | Required | Enum (`INCOME`, `EXPENSE`) | Transaction classification. | Must be one of the supported enum values. |
| `categoryId` | Required | Long | Identifier for the transaction category. | Must be greater than 0. |
| `memo` | Required | String | Human-readable description. | Must be 200 characters or fewer. |

## Example Data

```json
{
  "timestamp": "2024-06-15T10:24:30Z",
  "amount": 42.50,
  "currencyCode": "USD",
  "type": "EXPENSE",
  "categoryId": 101,
  "memo": "Weekly supermarket run"
}
```

## API
No transaction API endpoints are defined in the current codebase.
