# Front-End App (Calendar Application) — React + Spring Boot + Keycloak

This project demonstrates an OpenID Connect (OIDC) secured application using:

- **React + JavaScript** — frontend application
- **Spring Boot** — protected calendar REST API
- **Keycloak 26** — Identity Provider / Authorization Server
- **Authorization Code Flow with PKCE**
- **JWT access tokens**
- **Keycloak realm role authorization**
- **CORS configuration**
- Optional MFA/2FA authorization using a custom Keycloak authenticator and token claim

---

## 1. Project Structure

```text
oidc-demo/
├── frontend-app/          # React frontend
├── calendarService/       # Spring Boot REST API
├── keycloak/              # Keycloak configuration/customizations
├── themes/                # Optional Keycloak themes
├── docker-compose.yml     # Keycloak/container configuration
└── README.md
```

---

# 2. Prerequisites

Install the following:

- Java 21
- Maven
- Node.js and npm
- Docker / Docker Compose
- Git

Check the installations:

```bash
java -version
mvn -version
node -v
npm -v
docker --version
docker compose version
```

---

# 3. Start Keycloak

From the project root:

```bash
docker compose up -d
```

Check the containers:

```bash
docker compose ps
```

Keycloak should be available at:

```text
http://localhost:8080
```

Open the Keycloak Admin Console:

```text
http://localhost:8080/admin
```

Log in using the admin credentials configured in `docker-compose.yml`.

---

# 4. Keycloak Realm

Create/use the realm:

```text
calendar-realm
```

The application expects the issuer:

```text
http://localhost:8080/realms/calendar-realm
```

---

# 5. Create the `my-role` Realm Role

In Keycloak:

```text
Realm
  → Realm roles
  → Create role
```

Create:

```text
my-role
```

This role is required to access the calendar API.

---

# 6. Create Users

Create a test user, for example:

```text
Username: Swapnil
```

Set a password under:

```text
Users
  → Swapnil
  → Credentials
```

Assign the role:

```text
Users
  → Swapnil
  → Role mapping
  → Assign role
  → my-role
```

Create another user without `my-role` to test authorization.

For example:

```text
Username: user_without_role
```

Do not assign `my-role` to this user.

---

# 7. Configure the Frontend Client

Create/configure the Keycloak client:

```text
frontend-app
```

Recommended configuration:

```text
Client type:
OpenID Connect

Client authentication:
Off

Authorization:
Off

Standard flow:
On
```

The frontend uses:

```text
Authorization Code Flow + PKCE
```

with:

```text
PKCE method: S256
```

Configure the redirect URI used by the React application.

For the current project:

```text
http://localhost:3000/*
```

Configure the Web Origin:

```text
http://localhost:3000
```

---

# 8. Frontend Configuration

The React application uses Keycloak through `keycloak-js`.

The Keycloak configuration should point to:

```javascript
const keycloak = new Keycloak({
    url: "http://localhost:8080",
    realm: "calendar-realm",
    clientId: "frontend-app"
});
```

Initialize Keycloak using Authorization Code Flow with PKCE:

```javascript
keycloak.init({
    onLoad: "check-sso",
    pkceMethod: "S256"
});
```

---

# 9. Start the React Application

Go to the frontend directory:

```bash
cd frontend-app
```

Install dependencies:

```bash
npm install
```

Start the application:

```bash
npm start
```

The application should be available at:

```text
http://localhost:3000
```

The user will be redirected to Keycloak for authentication.

---

# 10. OIDC Login Flow

The login process is:

```text
Browser
   |
   | 1. Open frontend
   v
React frontend
   |
   | 2. Authorization request
   |    Authorization Code + PKCE
   v
Keycloak
   |
   | 3. Login
   | 4. Optional/required OTP
   v
Keycloak
   |
   | 5. Authorization code
   v
React
   |
   | 6. Exchange code using PKCE
   v
Keycloak
   |
   | 7. Access token
   v
React
```

The frontend then uses the access token when calling the calendar API.

---

# 11. Access Token

After successful login, the Keycloak access token can be inspected in the browser console.

For example:

```javascript
console.log(keycloak.tokenParsed);
```

To print the authentication context:

```javascript
console.log("ACR:", keycloak.tokenParsed?.acr);
console.log("AMR:", keycloak.tokenParsed?.amr);
```

Example:

```text
ACR: 1
AMR: []
```

The exact values depend on the Keycloak authentication flow and configured token mappers.

---

# 12. Spring Boot Calendar Service

The calendar service is a Spring Boot resource server.

Go to:

```bash
cd calendarService
```

Build the application:

```bash
mvn clean package
```

Run it:

```bash
mvn spring-boot:run
```

The API should be available on the port configured in:

```text
application.properties
```

or:

```text
application.yml
```

For example:

```text
http://localhost:8081
```

---

# 13. JWT Validation

The Spring Boot application validates the Keycloak access token.

The issuer should be configured as:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/calendar-realm
```

Spring Security obtains Keycloak's public keys from the issuer and validates the JWT locally.

The request therefore needs:

```http
Authorization: Bearer <access-token>
```

---

# 14. Calendar API Authorization

The calendar endpoint is:

```text
GET /calendar
```

The endpoint requires:

1. A valid JWT
2. The Keycloak realm role `my-role`

The existing security configuration converts:

```json
{
  "realm_access": {
    "roles": [
      "my-role"
    ]
  }
}
```

into:

```text
ROLE_my-role
```

Spring Security then uses:

```java
.hasRole("my-role")
```

---

# 15. Expected Authorization Behaviour

### User with `my-role`

```text
Valid JWT
   +
my-role
   ↓
200 OK
```

The calendar data is returned.

### User without `my-role`

```text
Valid JWT
   +
No my-role
   ↓
403 Forbidden
```

The API returns:

```json
{
  "status": 403,
  "message": "You are not authorized to view this resource"
}
```

### No/invalid token

```text
No valid JWT
   ↓
401 Unauthorized
```

---

# 16. CORS

The calendar service currently allows the React frontend:

```text
http://localhost:3000
```

Allowed methods include:

```text
GET
POST
PUT
DELETE
```

The frontend therefore can call the Spring Boot API from the browser.

---

# 17. Calling the API from React

The frontend should send the Keycloak access token as a Bearer token.

Example:

```javascript
const response = await fetch("http://localhost:8081/calendar", {
    headers: {
        Authorization: `Bearer ${keycloak.token}`
    }
});
```

The request received by Spring Boot looks like:

```http
GET /calendar
Authorization: Bearer eyJ...
```

---

# 18. Testing with Postman

First obtain a valid access token through the frontend.

For debugging, print:

```javascript
console.log(keycloak.token);
```

Copy the access token.

In Postman:

```text
Authorization
  → Type: Bearer Token
  → Token: <paste access token>
```

Then call:

```text
GET http://localhost:8081/calendar
```

### Expected

User with `my-role`:

```text
200 OK
```

User without `my-role`:

```text
403 Forbidden
```

Invalid/expired token:

```text
401 Unauthorized
```

---

# 19. MFA / 2FA Bonus

The bonus requirement is:

> Only users who have successfully authenticated using 2-factor authentication should be allowed to access the calendar service.

The intended architecture is:

```text
Username + Password
        |
        v
      OTP
        |
        v
Custom Keycloak Authenticator
        |
        | otp_verified = true
        v
User Session
        |
        v
Check ACR
        |
        | ACR = OTP
        |
        v
Spring Boot
```

The important distinction is that `ACR` will be set based on the ACR to LoA Mapping done on the Keycloak admin console.

```json
"ACR": Otp
```
OR

```json
"ACR": password
```

---

# 20. MFA Token Claim

The desired access token claim is:

```json
"ACR": Otp
```
OR

```json
"ACR": password
```

A password-only authentication should receive a ACR with "password".

The Spring Boot service can then enforce:

```text
my-role == required
AND
ACR == otp
```

---

# 21. Final Calendar Authorization

The final authorization model is:

```text
                    Request
                       |
                       v
                  Valid JWT?
                  /        \
                NO          YES
                |            |
               401           v
                         my-role?
                         /     \
                       NO       YES
                       |         |
                      403        v
                                 ACR
                              /       \
                           password    otp
                           |              |
                         403            200
                                         |
                                         v
                                   Calendar data
```

---

# 22. Troubleshooting

## SpringApplication cannot be resolved

Run:

```bash
mvn clean install
```

Then refresh the Maven project in VS Code.

Make sure Java 21 is being used:

```bash
java -version
```

---

## Invalid redirect URI

Check:

```text
Keycloak
→ Clients
→ frontend-app
→ Valid redirect URIs
```

Make sure the React URL is included:

```text
http://localhost:3000/*
```

---

## 403 for a user who should have access

Decode the access token and check:

```json
"realm_access": {
    "roles": [
        "my-role"
    ]
}
```

If `my-role` is missing, assign the role to the user in Keycloak.

---

## User without `my-role` receives 200

Check the Spring Security JWT converter.

The converter must read:

```text
realm_access.roles
```

and convert the role into:

```text
ROLE_my-role
```

The endpoint should use:

```java
.hasRole("my-role")
```

---

## Browser CORS error

Verify:

```text
http://localhost:3000
```

is allowed by the Spring Boot CORS configuration.

Also verify the browser is actually sending:

```http
Authorization: Bearer <token>
```

---

# 23. Stop the Applications

Stop React with:

```text
Ctrl+C
```

Stop Spring Boot with:

```text
Ctrl+C
```

Stop Keycloak:

```bash
docker compose down
```

To stop containers without removing persistent volumes:

```bash
docker compose down
```

Do not use `docker compose down -v` unless you intentionally want to remove the Docker volumes/data.

---

# 24. Start Everything Again

From the project root:

```bash
docker compose up -d
```

Then start Spring Boot:

```bash
cd calendarService
mvn spring-boot:run
```

In another terminal start React:

```bash
cd frontend-app
npm start
```

Open:

```text
http://localhost:3000
```

---

# 25. Security Summary

This project demonstrates:

- OpenID Connect
- OAuth 2.0 Authorization Code Flow
- PKCE with S256
- Keycloak authentication
- JWT access tokens
- Spring Security OAuth2 Resource Server
- JWT signature/issuer validation
- Keycloak realm roles
- Role-based authorization
- HTTP 401 vs 403 handling
- CORS
- Protected REST APIs
- Optional MFA enforcement
- Keycloak Authentication SPI
- Token claims and protocol mapping

The important security boundary is:

```text
Keycloak
   ↓
Authentication
   ↓
JWT
   ↓
Spring Security
   ↓
Authorization
   ↓
Calendar API
```

The React application is **not trusted to decide whether a user is authorized**. The calendar service validates the JWT and performs the authorization checks itself.
