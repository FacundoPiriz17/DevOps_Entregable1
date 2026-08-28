const priceFormatter = new Intl.NumberFormat("es-UY", {
  style: "currency",
  currency: "USD",
  minimumFractionDigits: 2,
});

export const formatPrice = (value) => priceFormatter.format(Number(value || 0));

export const formatDate = (value) => {
  if (!value) return "Sin fecha";
  return format(parseISO(value), "d 'de' MMMM 'de' yyyy", { locale: es });
};

export const gameImage = (game, preferredType = "portada") => {
  const images = game?.images || [];
  return images.find((image) => image.type === preferredType)?.url || images[0]?.url || null;
};

export const gameImageAlt = (game, preferredType = "portada") => {
  const images = game?.images || [];
  return images.find((image) => image.type === preferredType)?.alternativeText
    || images[0]?.alternativeText
    || `Imagen de ${game?.name || "videojuego"}`;
};

export const pickRandomGames = (games, amount = 5) => {
  const shuffled = [...games];

  for (let index = shuffled.length - 1; index > 0; index -= 1) {
    const values = new Uint32Array(1);
    globalThis.crypto.getRandomValues(values);
    const target = values[0] % (index + 1);
    [shuffled[index], shuffled[target]] = [shuffled[target], shuffled[index]];
  }

  return shuffled.slice(0, amount);
};

export const mergeGames = (entries, games) => entries.map((entry) => ({
  ...games.find((game) => game.id === entry.gameId),
  ...entry,
  id: entry.gameId,
}));
import { format, parseISO } from "date-fns";
import { es } from "date-fns/locale";
