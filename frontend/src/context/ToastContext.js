"use client";

import { createContext, useCallback, useContext, useMemo } from "react";
import { sileo, Toaster } from "sileo";

const ToastContext = createContext(null);

const toastOptions = {
  fill: "#0d1330",
  roundness: 18,
  duration: 4200,
  styles: {
    title: "!font-semibold !text-white",
    description: "!text-slate-300",
  },
};

export function ToastProvider({ children }) {
  const notify = useCallback((message, type = "info") => {
    const method = ["success", "error", "warning", "info"].includes(type) ? type : "info";
    sileo[method]({ ...toastOptions, title: message });
  }, []);

  const value = useMemo(() => ({ notify }), [notify]);

  return (
    <ToastContext.Provider value={value}>
      <Toaster position="top-right" offset={{ top: 82, right: 16 }} options={toastOptions}>
        {children}
      </Toaster>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) throw new Error("useToast debe utilizarse dentro de ToastProvider");
  return context;
}
