# Summary of Changes

## 1. RefreshTokenService Error Fix
- **Issue**: `RefreshTokenDetails` record had a `deviceId` field that was not used in the `validate` method, causing a constructor mismatch.
- **Fix**: 
  - Updated `RefreshTokenDetails` (src/main/java/com/shyam/common/dto/RefreshTokenDetails.java) to remove `deviceId` field.
  - Fixed a missing parenthesis in `AdminServiceImp.java` (line 145) when calling `refreshTokenService.delete()`.

## 2. getAllAdmin Method Enhancement
- **Requirement**: Retrieve both ADMIN and SUPER_ADMIN users.
- **Changes**:
  - Added `ArrayList` import to `AdminMapper.java`.
  - Implemented `getAllAdmin()` method in `AdminMapper.java` to:
    - Fetch ADMIN users via `adminDAO.findByRole(Role.ADMIN)`
    - Fetch SUPER_ADMIN users via `adminDAO.findByRole(Role.SUPER_ADMIN)`
    - Combine both lists
    - Map each to `GetAllAdminResponseDTO` using existing `mapToGetAllAdminDTO`
    - Return `GetAdminListResponseDTO` containing the combined list

## 3. Application Configuration Fix
- **Issue**: Duplicate `spring` key in `application.yml` causing YAML parsing error.
- **Fix**: Merged the two `spring` sections into a single section with `profiles` and `kafka` sub-sections.

## 4. Code Formatting
- Applied Spotless formatting to ensure consistent code style.

## Files Modified
1. src/main/java/com/shyam/common/dto/RefreshTokenDetails.java
2. src/main/java/com/shyam/service/Imp/AdminServiceImp.java
3. src/main/java/com/shyam/mapper/AdminMapper.java
4. src/main/resources/application.yml
5. src/main/java/com/shyam/listener/NotificationListener.java (previous work)
6. src/main/java/com/shyam/service/EmailService.java (previous work)
7. src/main/java/com/shyam/service/impl/EmailServiceImpl.java (previous work)

## Build Status
- Clean build successful
- Spotless formatting applied