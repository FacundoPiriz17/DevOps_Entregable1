"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { sessionStorage } from "@/lib/storage";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const router = useRouter();
  const [session, setSession] = useState(null);
  const [ready, setReady] = useState(false);

  const clearSession = useCallback(() => {
    sessionStorage.clear();
    setSession(null);
  }, []);

  const logout = useCallback(() => {
    clearSession();
    router.replace("/login");
  }, [clearSession, router]);

  useEffect(() => {
    const stored = sessionStorage.read();
    if (!stored) {
      Promise.resolve().then(() => setReady(true));
      return;
    }

    api.users.me()
      .then((user) => {
        const nextSession = { token: stored.token, user };
        sessionStorage.write(stored.token, user);
        setSession(nextSession);
      })
      .catch(clearSession)
      .finally(() => setReady(true));
  }, [clearSession]);

  useEffect(() => {
    const handleUnauthorized = () => {
      clearSession();
      router.replace("/login?expired=1");
    };

    window.addEventListener("playhub:unauthorized", handleUnauthorized);
    return () => window.removeEventListener("playhub:unauthorized", handleUnauthorized);
  }, [clearSession, router]);

  const saveAuthResponse = useCallback((response) => {
    const user = {
      name: response.name,
      email: response.email,
      country: response.country,
      role: response.role,
      active: true,
    };
    const nextSession = { token: response.token, user };
    sessionStorage.write(response.token, user);
    setSession(nextSession);
    return nextSession;
  }, []);

  const login = useCallback(async (credentials) => {
    const response = await api.auth.login(credentials);
    return saveAuthResponse(response);
  }, [saveAuthResponse]);

  const register = useCallback(async (data) => {
    const response = await api.auth.register(data);
    return saveAuthResponse(response);
  }, [saveAuthResponse]);

  const value = useMemo(() => ({
    session,
    user: session?.user || null,
    token: session?.token || null,
    ready,
    authenticated: Boolean(session?.token),
    isUser: session?.user?.role === "USER",
    isAdmin: session?.user?.role === "ADMIN",
    login,
    register,
    logout,
  }), [session, ready, login, register, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth debe utilizarse dentro de AuthProvider");
  return context;
}
