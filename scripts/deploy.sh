#!/usr/bin/env bash

set -euo pipefail

deploy_green=false

case "${1:-}" in
  "")
    ;;
  --deploy-green)
    deploy_green=true
    ;;
  *)
    echo "Uso: $0 [--deploy-green]" >&2
    exit 2
    ;;
esac

if ! command -v kubectl >/dev/null 2>&1; then
  echo "No se encontro kubectl en PATH." >&2
  exit 1
fi

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd "$script_dir/.." && pwd)"
migration_directory="$project_root/backend/src/main/resources/db/migration"

if [[ ! -d "$migration_directory" ]]; then
  echo "No se encontro el directorio de migraciones: $migration_directory" >&2
  exit 1
fi

cd "$project_root"

echo "Aplicando configuracion y PostgreSQL..."

kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/postgres-pvc.yaml
kubectl apply -f k8s/postgres-service.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl rollout status deployment/playhub-postgres --timeout=180s

echo "Cargando las migraciones SQL en Kubernetes..."

kubectl create configmap playhub-migration-sql \
  --from-file=backend/src/main/resources/db/migration \
  --dry-run=client \
  -o yaml | kubectl apply -f -

echo "Ejecutando el Job de Flyway..."

kubectl delete job playhub-migrations --ignore-not-found=true
kubectl apply -f k8s/migration-job.yaml
kubectl wait --for=condition=complete job/playhub-migrations --timeout=300s
kubectl logs job/playhub-migrations

echo "Desplegando Services y el slot Blue..."

kubectl apply -f k8s/backend-service.yaml
kubectl apply -f k8s/frontend-service.yaml
kubectl apply -f k8s/backend-blue-deployment.yaml
kubectl apply -f k8s/frontend-blue-deployment.yaml
kubectl rollout status deployment/playhub-backend-blue --timeout=180s
kubectl rollout status deployment/playhub-frontend-blue --timeout=180s

if [[ "$deploy_green" == true ]]; then
  echo "Desplegando tambien el slot Green..."

  kubectl apply -f k8s/backend-green-deployment.yaml
  kubectl apply -f k8s/frontend-green-deployment.yaml
  kubectl rollout status deployment/playhub-backend-green --timeout=180s
  kubectl rollout status deployment/playhub-frontend-green --timeout=180s
fi

echo
echo "Despliegue terminado. Slot activo: Blue"

kubectl get pods
kubectl get jobs
kubectl get pvc
kubectl get services
