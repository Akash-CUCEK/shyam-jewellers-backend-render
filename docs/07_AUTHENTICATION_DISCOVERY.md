# Shyam Jewellers Backend - Authentication Discovery

## Authentication Models

The backend currently has two authentication models:

| Principal Type | Table | Login Method | Token Role |
|---|---|---|---|
| Admin | `admin_users` | Email + password | `ADMIN` or `SUPER_ADMIN` |
| User | `users` | Email + OTP | `USER` |

Admins and users are separate database entities and repositories.

## JWT Access Token

Implemented in `JwtUtil`.

| Attribute | Current Behavior |
|---|---|
| Token library | `io.jsonwebtoken` |
| Algorithm | HS256 |
| Secret source | `jwt.secret` property |
| Minimum secret length | Runtime check requires at least 32 bytes |
| Subject | Email |
| Claims | `role` |
| Issued-at | Present |
| Expiry | 24 hours |
| Refresh token | UUID string, not JWT |
| Validation | Signature/expiry parse plus Redis blacklist check |

Access-token helper methods:

- `generateAccessToken(username, role)`
- `getUsername(token)`
- `getRole(token)`
- `getExpiry(token)`
- `validateToken(token)`

## Refresh Token

Implemented in `RefreshTokenService` and `RefreshTokenController`.

| Attribute | Current Behavior |
|---|---|
| Token format | UUID string |
| Storage | Redis |
| Storage value | BCrypt hash of refresh token |
| Key format | `refresh:{role}:{email}:{deviceId}` |
| TTL | 1 day |
| Validation | Redis key lookup plus BCrypt match |
| Rotation | Delete old key, generate new UUID token, store new hash |
| Web transport | `refreshToken` HTTP-only cookie |
| Mobile transport | Request body supports refresh token |

`/refreshToken` currently requires:

```text
refreshToken
email
role
deviceId
```

Although the controller reads the cookie token when the body is absent, email/role/deviceId are still required and become null without a body.

## Admin Login Flow

Endpoint:

```text
POST /auth/api/v1/admin/logIn
```

Request DTO:

```text
AdminLogInRequestDTO
  email
  password
  deviceId
```

Flow:

```text
AdminController.logIn
  -> AdminServiceImp.logIn
    -> AdminDAO.findUserByEmail
      -> AdminRepository.findByEmail
    -> BCryptPasswordEncoder.matches(request.password, admin.password)
    -> JwtUtil.generateAccessToken(admin.email, admin.role.name)
    -> JwtUtil.generateRefreshToken()
    -> RefreshTokenService.store(admin.email, role, refreshToken, deviceId/default-device)
    -> response body with token and refreshToken
    -> Set-Cookie refreshToken
```

Cookie properties:

```text
httpOnly=true
secure=false
sameSite=Lax
path=/
maxAge=1 day
```

## User OTP Login Flow

Start endpoint:

```text
POST /api/v1/auth/logIn
```

Request DTO:

```text
logInRequestDTO
  email
```

Start flow:

```text
UserController.register
  -> UserServiceImp.logIn
    -> UserMapper.logInMapper
      -> UserDAO.findOnlyUser
        -> UsersRepo.findByEmail
      -> generate 6-digit OTP
      -> create or update Users.otp and otpGeneratedTime
      -> UserDAO.save
      -> EmailService.sendEmail
```

Verify endpoint:

```text
POST /api/v1/auth/verify
```

Request DTO:

```text
OtpRequestDTO
  email
  otp
  deviceId
```

Verify flow:

```text
UserController.verify
  -> UserServiceImp.verify
    -> UserDAO.findUser
      -> UsersRepo.findByEmail
    -> validate otpGeneratedTime within 5 minutes
    -> compare request OTP to stored OTP
    -> JwtUtil.generateAccessToken(user.email, USER)
    -> JwtUtil.generateRefreshToken()
    -> RefreshTokenService.store(user.email, USER, refreshToken, deviceId/default-device)
    -> response body with token and refreshToken
    -> Set-Cookie refreshToken
```

Cookie properties:

```text
httpOnly=true
secure=true
sameSite=None
path=/
maxAge=1 day
```

## Admin Password Reset Flow

Start endpoint:

```text
POST /auth/api/v1/admin/forgetPassword
```

Flow:

```text
AdminServiceImp.forgetPassword
  -> find admin by email
  -> generate 6-digit OTP
  -> save otp and otpGeneratedTime
  -> send OTP email
```

Verify endpoint:

```text
POST /auth/api/v1/admin/verifyPasswordOtp
```

Flow:

```text
AdminServiceImp.forgetVerifyOtp
  -> find admin by email
  -> validate OTP timestamp within 5 minutes
  -> compare OTP value
  -> encode new password
  -> clear OTP and timestamp
  -> save admin
```

Observed implementation detail:

The same-password check in `forgetVerifyOtp` calls `passwordEncoder.matches(admin.getPassword(), password)` where `admin.getPassword()` is already the stored encoded hash and `password` is the newly encoded password.

## Admin Change Password Flow

Endpoint:

```text
POST /auth/api/v1/admin/changePassword
```

Flow:

```text
AdminServiceImp.changePassword
  -> find admin by email
  -> BCrypt old password check
  -> BCrypt new password differs from current hash
  -> encode and save new password
```

This endpoint requires:

```text
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
```

## Logout Flow

Active logout endpoint:

```text
POST /auth/api/v1/admin/logout
```

Inputs:

```text
Authorization: Bearer {accessToken}
Cookie: refreshToken={refreshToken}
X-Device-Id: {deviceId}
```

Flow:

```text
AdminController.logout
  -> strip Bearer prefix
  -> AdminServiceImp.logout
    -> calculate remaining access-token lifetime
    -> TokenBlacklistService.blacklistToken(accessToken, remainingSeconds)
    -> if refresh token exists:
         RefreshTokenService.delete(email, role, deviceId/default-device)
  -> delete refreshToken cookie
```

Delete-cookie properties:

```text
httpOnly=true
secure=true
sameSite=Strict
path=/
maxAge=0
```

User logout is not active because the controller and service methods are commented out.

## Roles and Authorities

Role enum:

```text
USER
ADMIN
SUPER_ADMIN
```

Admin authorities:

```text
ROLE_ADMIN
ROLE_SUPER_ADMIN
```

Normal user authority currently returned by `NormalUserDetailsService`:

```text
USERS
```

This differs from Spring's `hasRole('USER')` convention, which expects `ROLE_USER`.

## Website and Mobile Authentication Support

Current code supports both token-in-body and refresh-cookie patterns:

- Admin login returns access token and refresh token in response body and also sets refresh cookie.
- User verify returns access token and refresh token in response body and also sets refresh cookie.
- Refresh endpoint reads either request body refresh token or cookie token.

Current implementation still requires email, role, and deviceId for refresh validation.

## Password Handling

| Area | Current Behavior |
|---|---|
| Admin stored password | BCrypt hash |
| New admin registration | BCrypt encode before save |
| Admin login | BCrypt match |
| Admin change password | BCrypt match old password and encode new password |
| Admin password reset | BCrypt encode new password |
| User authentication | No password; OTP-only |

## Session State

Session state is externalized to Redis:

| State | Redis Key |
|---|---|
| Refresh token hash | `refresh:{role}:{email}:{deviceId}` |
| Access token blacklist | `blacklisted_token:{token}` |

No database table for sessions or refresh tokens exists.

