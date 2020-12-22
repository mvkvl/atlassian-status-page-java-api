# Atlassian Statuspage Java API

## General description 

This library implements a subset of Atlassian Statuspage REST API to simplify its usage from Java applications. 

The simplest way to use this library is to include it as a dependency into your project like this:
```maven
<dependency>
    <groupId>ws.slink</groupId>
    <artifactId>atlassian-status-page-java-api</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Supported API methods
This library supports following funcitonality:
- List Objects
    - Pages
    - Groups
    - Components
    - Group Components
    - Incidents
- Get Object
    - Page
    - Group
    - Component
    - Incident
- Create Object
    - Component
    - Group
    - Incident
- Update Object
    - Component
    - Group
    - Incident
- Delete Object
    - Component
    - Group
    - Incident

The library can simply be extended to add support for additional Statuspage REST API methods.

## Usage
> Full usage examples you can find in `test` directory of this project.

### 1. Create StatusAPI object

At first you need to create StatusPage object:

```java
    StatusPage statusPage = new StatusPage.Builder()
        .apiKey(System.getenv("STATUSPAGE_API_KEY"))
        .bridgeErrors(true)
        .rateLimit(true)
        .rateLimitDelay(1000)
        .build()
    ;
```
`rateLimit` and `rateLimitDelay` here are needed to slow down API calls to conform to StatusPage REST API rules. 
If you're not going to perform too many calls to API, you can omit setting this variables (by default `rateLimit` 
is disabled). 

`bridgeErrors` here is needed to throw API call exceptions out of library to client code, so that client could handle 
all the errors by itself. Otherwise all exceptions are handled in the library and empty objects are returned as a result.

### 2. Use StatusPage API
Next you can use created object in straightforward manner:

- get all the pages: 
    ```java
    Page page = statusPage.pages();
    ```
  
- get page components: 
    ```java
    List<Component> components = statusPage.component(page);
    ```

- get one component:
    ```java
    Optional<Component> component = resource.statusPage().getComponent(page.id(), componentId, true);
    ```
    Final boolean flag in this (and similar) call(s) is used to ask the library to perform additional API calls to 
    synchronize full information about the object requested. Without this flag set to `true` only partial information 
    can be requested and populated into domain model object.

- get groups of components registered for the page: 
    ```java
    List<Group> groups = statusPage.groups(page);
    ```

- create incident:
    ```java
        statusPage.createIncident(
            page.id(),
            "<INCIDENT TITLE>",
            "<INCIDENT DESRIPTION>",
            IncidentSeverity.MAJOR
        ).ifPresentOrElse(System.out::println, () -> System.out.println("could not create incident"));
    ```

- update incident:
    ```java
        Optional<Incident> incident = statusPage.getIncident(pageId, incidentId);
        incident.get().impact(IncidentSeverity.MINOR);
        incident.get().status(IncidentStatus.RESOLVED);
        incident.get().components().stream().forEach(c -> c.status(ComponentStatus.OPERATIONAL));
        Optional<Incident> updated = statusPage.updateIncident(incident.get());
    ```
  
