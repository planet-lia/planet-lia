# Online Editor

TODO - add diagrams

### Status
* Queued
* Generating
* Successful
* Failed

## Backend Core Endpoints
* `POST` `/internal/online-editor/match/{matchId}/state`
    * Headers
        * Required:
            * Authorization: Bearer \<JWT TOKEN\>
            * Content-Type: application/json
    * Body
        * status: \<generating | successful | failed\>
        * log: \<base64 encoded\>
        * replay: \<base64 encoded\>
    * Response:
        * `HTTP 200`: match updated successfully
        * `HTTP 204`: match has expired, stop generating
        
* `GET` `/internal/online-editor/match/{matchId}/bot/{bot}`
    * Headers
            * Required:
                * Authorization: Bearer \<JWT TOKEN\>
    * Response: base64 encoded source code of bot
