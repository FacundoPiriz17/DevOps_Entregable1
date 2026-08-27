import { sessionStorage } from "./storage";

const API_URL = (process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080").replace(/\/$/, "");

const defaultMessages = {
  0: "No se pudo conectar con el backend. Comprobá que esté levantado.",
  400: "La solicitud contiene datos inválidos.",
  401: "Tu sesión venció o las credenciales no son correctas.",
  403: "No tenés permisos para realizar esta acción.",
  404: "No se encontró el recurso solicitado.",
  409: "La operación entra en conflicto con el estado actual.",
  500: "Ocurrió un error inesperado en el servidor.",
};

const translatedCodes = {
  INVALID_CREDENTIALS: "El correo o la contraseña no son correctos.",
  ACCESS_DENIED: "No tenés permisos para realizar esta acción.",
  EMAIL_ALREADY_USED: "Ya existe una cuenta con ese correo.",
  ACCOUNT_INACTIVE: "Esta cuenta fue desactivada.",
  ALREADY_IN_CART: "Este juego ya está en tu carrito.",
  ALREADY_IN_LIBRARY: "Este juego ya forma parte de tu biblioteca.",
  ALREADY_IN_WISHLIST: "Este juego ya está en tu lista de deseados.",
  GAME_NOT_AVAILABLE: "Este juego no está disponible para comprar.",
  GAME_RETIRED: "Los juegos retirados no pueden agregarse a deseados.",
  EMPTY_CART: "El carrito está vacío.",
};

export class ApiError extends Error {
  constructor(status, code, message, payload = null) {
    super(translatedCodes[code] || message || defaultMessages[status] || `Error ${status}`);
    this.name = "ApiError";
    this.status = status;
    this.code = code;
    this.payload = payload;
  }
}

async function request(path, { method = "GET", body, auth = true } = {}) {
  const headers = { Accept: "application/json" };
  const token = sessionStorage.token();

  if (body !== undefined) headers["Content-Type"] = "application/json";
  if (auth && token) headers.Authorization = `Bearer ${token}`;

  let response;

  try {
    response = await fetch(`${API_URL}${path}`, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch {
    throw new ApiError(0, "NETWORK_ERROR", defaultMessages[0]);
  }

  if (response.status === 401 && auth && token && typeof window !== "undefined") {
    window.dispatchEvent(new Event("playhub:unauthorized"));
  }

  if (!response.ok) {
    let payload = null;
    try {
      payload = await response.json();
    } catch {
      payload = null;
    }

    throw new ApiError(
      response.status,
      payload?.code,
      payload?.message || defaultMessages[response.status],
      payload,
    );
  }

  if (response.status === 204) return null;
  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

export const api = {
  auth: {
    login: (credentials) => request("/api/auth/login", { method: "POST", body: credentials, auth: false }),
    register: (data) => request("/api/auth/register", { method: "POST", body: data, auth: false }),
  },
  users: {
    me: () => request("/api/users/me"),
  },
  games: {
    list: () => request("/api/games"),
    get: (id) => request(`/api/games/${id}`),
  },
  categories: {
    list: () => request("/api/categories"),
  },
  cart: {
    list: () => request("/api/cart"),
    add: (gameId) => request(`/api/cart/games/${gameId}`, { method: "POST" }),
    remove: (gameId) => request(`/api/cart/games/${gameId}`, { method: "DELETE" }),
    checkout: () => request("/api/cart/checkout", { method: "POST" }),
  },
  library: {
    list: () => request("/api/library"),
    add: (gameId) => request(`/api/library/games/${gameId}`, { method: "POST" }),
    favorite: (gameId, favorite) => request(`/api/library/games/${gameId}/favorite`, {
      method: "PATCH",
      body: { favorite },
    }),
  },
  wishlist: {
    list: () => request("/api/wishlist"),
    add: (gameId) => request(`/api/wishlist/games/${gameId}`, { method: "POST" }),
    remove: (gameId) => request(`/api/wishlist/games/${gameId}`, { method: "DELETE" }),
  },
};
