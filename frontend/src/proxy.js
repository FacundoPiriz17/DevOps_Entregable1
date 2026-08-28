import { NextResponse } from "next/server";

const AUTH_COOKIE = "playhub_session";
const AUTH_ROUTES = new Set(["/login", "/register"]);

export function proxy(request) {
  const { pathname } = request.nextUrl;
  const authenticated = Boolean(request.cookies.get(AUTH_COOKIE)?.value);

  if (authenticated && AUTH_ROUTES.has(pathname)) {
    return NextResponse.redirect(new URL("/catalog", request.url));
  }

  if (!authenticated && !AUTH_ROUTES.has(pathname)) {
    const loginUrl = new URL("/login", request.url);
    loginUrl.searchParams.set("next", pathname);
    return NextResponse.redirect(loginUrl);
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    "/login",
    "/register",
    "/catalog/:path*",
    "/games/:path*",
    "/library/:path*",
    "/wishlist/:path*",
    "/cart/:path*",
  ],
};
