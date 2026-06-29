## RabbitMQ Contract

### Exchange
--- 
- message.events

### Queues
--- 
- email.queue
- audit.queue

### Routing Keys
--- 
- message.created
- message.read
- message.acknowledged
- message.label.added
- message.label.removed

### Bindings
---

| Queue | Binding |
| -------- | -------- | 
| **email.queue** | message.created | 
| **audit.queue**    | message.*   | 


### Schema
```json
{
  "eventId": "UUID",
  "eventType": "MESSAGE_CREATED | MESSAGE_READ | MESSAGE_ACKNOWLEDGED | LABEL_ADDED | LABEL_REMOVED",
  "occurredAt": "ISO-8601 TIMESTAMP",
  "messageId": "UUID",
  "senderId": "UUID",
  "recipientId": "UUID",
  "label": "STRING (optional)"
}
```