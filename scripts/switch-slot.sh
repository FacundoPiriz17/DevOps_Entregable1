#!/usr/bin/env bash

set -euo pipefail

target="${1:-}"

if [[ "$target" != "blue" && "$target" != "green" ]]; then
  echo "Uso: $0 <blue|green>" >&2
  exit 2
fi

if ! command -v kubectl >/dev/null 2>&1; then
  echo "No se encontro kubectl en PATH." >&2
  exit 1
fi

get_service_slot() {
  kubectl get service "$1" -o jsonpath='{.spec.selector.slot}'
}

backend_deployment="playhub-backend-$target"
frontend_deployment="playhub-frontend-$target"

current_backend_slot="$(get_service_slot playhub-backend)"
current_frontend_slot="$(get_service_slot playhub-frontend)"

if [[ "$current_backend_slot" != "$current_frontend_slot" ]]; then
  echo "Estado inconsistente: frontend=$current_frontend_slot, backend=$current_backend_slot." >&2
  exit 1
fi

current_slot="$current_backend_slot"

if [[ "$current_slot" == "$target" ]]; then
  echo "PlayHub ya utiliza el slot '$target'."
  exit 0
fi

echo "Comprobando que Backend $target este disponible..."
kubectl rollout status "deployment/$backend_deployment" --timeout=180s

echo "Comprobando que Frontend $target este disponible..."
kubectl rollout status "deployment/$frontend_deployment" --timeout=180s

target_patch="$(printf '{"spec":{"selector":{"slot":"%s"}}}' "$target")"
rollback_patch="$(printf '{"spec":{"selector":{"slot":"%s"}}}' "$current_slot")"

echo "Cambiando el backend de $current_slot a $target..."
kubectl patch service playhub-backend --type=merge --patch="$target_patch"

echo "Cambiando el frontend de $current_slot a $target..."

if ! kubectl patch service playhub-frontend --type=merge --patch="$target_patch"; then
  echo "Fallo el cambio del frontend. Restaurando el backend a $current_slot..." >&2
  kubectl patch service playhub-backend --type=merge --patch="$rollback_patch"
  exit 1
fi

echo
echo "PlayHub ahora utiliza el slot '$target'."

kubectl get service playhub-backend playhub-frontend \
  -o custom-columns='SERVICE:.metadata.name,SLOT:.spec.selector.slot'

echo "Aviso: si utilizas port-forward, detenlo y vuelvelo a iniciar para conectarte a los Pods del nuevo slot."
