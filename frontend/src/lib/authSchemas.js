import { z } from "zod";

export const loginSchema = z.object({
  email: z.string().trim().min(1, "Ingresá tu correo.").email("Ingresá un correo válido."),
  password: z.string().min(1, "Ingresá tu contraseña."),
});

export const registerSchema = z.object({
  name: z.string().trim().min(2, "Ingresá al menos 2 caracteres."),
  email: z.string().trim().min(1, "Ingresá tu correo.").email("Ingresá un correo válido."),
  country: z.string().trim().min(2, "Ingresá tu país."),
  password: z.string().min(8, "La contraseña debe tener al menos 8 caracteres."),
  confirmPassword: z.string().min(1, "Repetí la contraseña."),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Las contraseñas no coinciden.",
  path: ["confirmPassword"],
});
