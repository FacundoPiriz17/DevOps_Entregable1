"use client";

import { useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";

export function useGames() {
  return useQuery({
    queryKey: ["games"],
    queryFn: api.games.list,
  });
}

export function useCategories() {
  return useQuery({
    queryKey: ["categories"],
    queryFn: api.categories.list,
  });
}

export function useGame(id) {
  const queryClient = useQueryClient();

  return useQuery({
    queryKey: ["games", String(id)],
    queryFn: () => api.games.get(id),
    enabled: Boolean(id),
    initialData: () => queryClient.getQueryData(["games"])
      ?.find((game) => String(game.id) === String(id)),
    initialDataUpdatedAt: () => queryClient.getQueryState(["games"])?.dataUpdatedAt,
  });
}
