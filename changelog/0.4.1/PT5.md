# PT5

Version: 0.4.1
Date: 2026-11-17

## Added

- feat: add NetworkModule with OkHttp and Retrofit setup
- feat: add HMAC signing for /challenge/get
- feat: add SessionInterceptor to automatically fetch challenge and add session to dhikr
- feat: add SessionAuthenticator to automatically refresh challenge on 401
- feat: add HmacSigningInterceptor with HMAC signing only for challenge requests

## Fixed

- fix: HMAC string-to-sign — timestamp, method, path, and body
- fix: HMAC key — use raw 64 bytes
