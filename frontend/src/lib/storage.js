export const TOKEN_KEY = "playhub.token";
export const USER_KEY = "playhub.user";

const hasWindow = () => typeof window !== "undefined";

export const sessionStorage = {
  read() {
    if (!hasWindow()) return null;

    const token = window.localStorage.getItem(TOKEN_KEY);
    const serializedUser = window.localStorage.getItem(USER_KEY);
    if (!token || !serializedUser) return null;

    try {
      return { token, user: JSON.parse(serializedUser) };
    } catch {
      this.clear();
      return null;
    }
  },
  write(token, user) {
    if (!hasWindow()) return;
    window.localStorage.setItem(TOKEN_KEY, token);
    window.localStorage.setItem(USER_KEY, JSON.stringify(user));
  },
  clear() {
    if (!hasWindow()) return;
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.removeItem(USER_KEY);
  },
  token() {
    return hasWindow() ? window.localStorage.getItem(TOKEN_KEY) : null;
  },
};
