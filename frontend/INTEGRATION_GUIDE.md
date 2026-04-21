# Consumer Authentication Integration Guide

## Frontend Setup Complete ✅

The Angular frontend is now configured with Consumer registration and login functionality. 

## Backend Requirements

Your Spring Boot backend needs to implement the following endpoints:

### 1. **Consumer Registration Endpoint**
```
POST /api/consumers/register
```

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "password": "securepassword",
  "confirmPassword": "securepassword"
}
```

**Success Response (200):**
```json
{
  "errorCode": "200",
  "errorDesc": "Registration Successful",
  "token": "jwt_token_here",
  "consumer": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890"
  }
}
```

**Error Response (Any other code):**
```json
{
  "errorCode": "400",
  "errorDesc": "Email already exists"
}
```

---

### 2. **Consumer Login Endpoint**
```
POST /api/consumers/login
```

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "securepassword"
}
```

**Success Response (200):**
```json
{
  "errorCode": "200",
  "errorDesc": "Login Successful",
  "token": "jwt_token_here",
  "consumer": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890"
  }
}
```

**Error Response:**
```json
{
  "errorCode": "401",
  "errorDesc": "Invalid email or password"
}
```

---

## Frontend Configuration

### API Base URL
The frontend is configured to use: `http://localhost:8080/api`

**To change this**, edit [auth.service.ts](src/app/auth/services/auth.service.ts):
```typescript
private apiUrl = 'http://localhost:8080/api'; // Change this URL
```

### Authentication Flow

1. **Register**: User fills form → Sent to `/api/consumers/register` → Token stored in localStorage
2. **Login**: User enters credentials → Sent to `/api/consumers/login` → Token stored in localStorage
3. **Auto-redirect**: After successful auth → Routes to `/dashboard`

### Token Storage

Tokens are automatically stored in `localStorage` with key: `token`

To access the token in other services:
```typescript
const token = this.authService.getToken();
```

### Current User

Current user info is stored in `localStorage` with key: `currentUser`

To get current user:
```typescript
const user = this.authService.getCurrentUser();
```

---

## Routes

| Route | Component | Purpose |
|-------|-----------|---------|
| `/auth/register` | RegisterComponent | Consumer registration form |
| `/auth/login` | LoginComponent | Consumer login form |
| `/dashboard` | (To be created) | Post-auth dashboard |

---

## Next Steps

1. ✅ **Frontend is ready** - All forms and services configured
2. ⏳ **Implement backend endpoints** - Create the Spring Boot controller endpoints
3. ⏳ **Test integration** - Test register and login flows
4. ⏳ **Add JWT validation** - Verify tokens on protected routes
5. ⏳ **Create dashboard** - Build the post-login dashboard

---

## CORS Configuration (If Needed)

If you encounter CORS errors, add this to your Spring Boot application:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

---

## Testing the Integration

1. Start Spring Boot backend: `mvn spring-boot:run` (runs on port 8080)
2. Start Angular frontend: `ng serve` (runs on port 4200)
3. Navigate to: `http://localhost:4200/auth/register`
4. Fill in the form and submit
5. Check browser console for any errors
6. Verify token is stored in localStorage (DevTools → Application → LocalStorage)

---

## Error Handling

- **Registration errors** are displayed in red alert boxes
- **Login errors** are displayed in red alert boxes
- **Console logs** show detailed error information for debugging
- Backend should return `errorCode` and `errorDesc` for user-friendly messages
