# OverOps Threshold Alert

## Summary

[(${event.data.summary})]

## Details

View Name: `[(${event.data.viewName})]`
Alert Added By: `[(${event.data.addedByString()})]`
Threshold: `[(${event.data.payload.threshold})]`
Occurrences: `[(${event.data.payload.times})]`
From: `[(${event.data.payload.getFromString()})]`
To: `[(${event.data.payload.getToString()})]`
Duration: `[(${event.data.payload.getDurationInMinutes()})] minutes`

## Top Events

[# th:each="topEvent : ${event.data.payload.topEvents}"]
    * Event `[(${topEvent.title})]` occurred `[(${topEvent.times})]` [times]([(${topEvent.link})])
[/]


